/*
 *
 * Client program accepts bytes from file and form into segments and then send to buffer to 
 * be tranfered using sliding window protocol
 */
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
public class Client
{
    private Segment [] buffer;//local buffer used in the sliding window protocol, used as window 
    private Timer [] timer;
    private int bufSize;
    private String hostName;//host name 
    private InetAddress serverIp;
    private int serverPort;//port number of the server
    private DatagramSocket socket = null; 
    private String fileName;//name of the file to be transfered
    private int N;  //window size
    private int MSS;//maximum segment size
    private int winHead;//pointer to the head of the window
    private int winCur;// pointer to the current byte of the window
    private int winTail;// pointer to the tail of the window
    private int bufHead;//pointer to the head of the buffer
    private int bufTail;// pointer to the tail of the buffer
    private int headerLen = 8;// length of the header(in byte)
    private int segSize;
    private Semaphore mutex = new Semaphore(1);
    private Semaphore empty = new Semaphore(bufSize);
    private Semaphore item = new Semaphore(0);

    public Client(String hostName, int serverPort, String fileName, int N, int MSS)
    {
        this.hostName = hostName;
        this.serverPort = serverPort;
        this.fileName = fileName;
        this.N = N;
        this.MSS = MSS;
        this.bufSize = N;
        this.segSize = MSS + headerLen;
        this.buffer = new Segment[bufSize];
        this.timer = new Timer[bufSize];
        try{
            this.serverIp = InetAddress.getByName(hostName);
            this.socket = new DatagramSocket(serverPort, serverIp);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
     * Initiliaze the buffer
     */
    public void initBuf()
    {
        this.bufHead = 0;
        this.bufTail = 0;
        this.winHead = 0;
        this.winCur  = 0;
        this.winTail = 0;
    }

    public void start()
    {
        initBuf();
        SlidingWindow sw = new SlidingWindow();
        sw.start();

        try(FileInputStream fio = new FileInputStream(fileName))
        {
            byte [] temp = new byte[MSS+headerLen];
            int seqno = 0;
            int num = 0;
            while( (num = fio.read(temp, headerLen, MSS)) != -1 )
            {
                send(new Segment(seqno, num, temp));
                seqno = seqno + num;
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    /*
     * put the segment into the buffer(window and send it) 
     */
    public void send(Segment seg)
    {
        try
        {
            empty.acquire();
            mutex.acquire();
            buffer[bufTail] = seg;

            //send the segment to the server
            DatagramPacket packet = new DatagramPacket(seg.get(), seg.size(), serverIp, serverPort);
            socket.send(packet);

            //Start the retransmision timer
            RetransTimer t = new RetransTimer(bufTail);
            timer[bufTail].schedule(t, 200);
            bufTail = (bufTail + 1) % bufSize;
        
            mutex.release();
            item.release();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String args[])
    {
        if(args.length != 5 )
        {
            System.err.println("Invalid Parameter");
            System.exit(1);
        }
        Client c = new Client(args[0], Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]));
        c.start();

    }

    /*
     * Timer task
     */
    public class RetransTimer extends TimerTask
    {
        private int index;
        public RetransTimer(int index)
        {
            this.index = index;
        }
        /*
         * If the timer expires, retransmit the segment
         */
        @Override
        public void run()
        {
            try{
                //send the segment to the server
                DatagramPacket packet = new DatagramPacket(buffer[index].get(), buffer[index].size(), serverIp, serverPort);
                socket.send(packet);
    
                //Start the retransmision timer
                timer[bufTail].schedule(new RetransTimer(index), 200);
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /*
     * Class to handle sliding window
     */
    public class SlidingWindow extends Thread
    {
        private boolean running = true;
        public SlidingWindow()
        {
            super();
        }
        /* Receive datagram from server, if ack, advance window. 
         * Whenever send a byte, start a timer for it. If overdue, resend the byte
         */
        @Override
        public void run()
        {
            while(running)
            {
                try
                {
                    byte[] buf = new byte[segSize];
                    // receive segments from server 
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    // construct the segment and process it 
                    Segment seg = new Segment(packet.getData(), packet.getLength());
                    if(seg.type() == 1)
                    {
                        if(seg.getSeqNo() == buffer[bufHead].getSeqNo() + buffer[bufHead].size())
                            consume(seg);
                        else if(seg.getSeqNo() == buffer[bufHead].getSeqNo())
                            ;//retransmit
                    }
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void consume(Segment seg)
        {
            try{
                item.acquire();
                mutex.acquire();
    
                //set the status, if the head of the buffer, remove, cancel the timer, else do nothing
                timer[bufHead].cancel();
                bufHead = (bufHead + 1) % bufSize;
        
                mutex.release();
                empty.release();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        }
}
