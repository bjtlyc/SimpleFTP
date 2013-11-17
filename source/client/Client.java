/*
 *
 * Client program accepts bytes from file and form into segments and then send to buffer to 
 * be tranfered using sliding window protocol
 */
import java.util.*;
import java.net.*;
import java.io.*;
public class Client
{
    private Segement [] buffer;//local buffer used in the sliding window protocol, used as window 
    private Timer [] timer;
    private int bufSize;
    private String hostName;//host name 
    private int serverPort;//port number of the server
    private DatagramSocket socket = null; 
    private fileName;//name of the file to be transfered
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
        buffer = new Segment[bufSize];
        timer = new Timer[bufSize];
        socket = new DatagramSocket(serverPort, hostName);
        segSize = MSS + headerLen;
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
        SlidingWindow sw = new SlidingWindow(socket, buffer, mutex, empty, item, segSize);
        sw.start();

        try(FileInputStream fio = new FileInputStream(fileName))
        {
            byte [] temp = new byte[MSS];
            while( read(temp) != -1 )
            {
                send(new Segment(temp));
            }
        }

    }

    /*
     * put the segment into the buffer(window and send it) 
     */
    public void send(Segment seg)
    {
        empty.acquire();
        mutex.acquire();
        buffer[bufTail] = seg;

        //send the segment to the server
        InetAddress address = InetAddress.getByName(hostName);
        DatagramPacket packet = new DatagramPacket(seg.seg, seg.seg.length, address, portNum);
        socket.send(packet);

        //Start the retransmision timer
        RetransTimer t = new RetransTimer(bufTail);
        timer[bufTail].schedule(t, 200);

        bufTail = (bufTail + 1) % bufSize;
        
        mutex.release();
        full.release();
    }

    public static void main(String args[])
    {
        if(args.length != 5 )
        {
            System.err.println("Invalid Parameter");
            System.exit(1);
        }
        Client c = new Client(args[0], args[1], args[2], args[3], args[4]);
        c.start();

    }

    /*
     * Data structure for segment
     */
    public static class Segment
    {
        public int seqno;
        public short checksum;
        public Status status;
        public [] byte seg;
        public Segment(byte[] seg)
        {
            this.seg = seg;
        }
    }
    /*
     * Timer task
     */
    public static class RetransTimer extends TimerTask
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
            
        }
    }
}
