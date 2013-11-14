/*
 *
 * Client program accepts bytes from file and form into segments and then send to buffer to 
 * be tranfered using sliding window protocol
 */
import java.util.*;
public class Client
{
    private byte [] buffer;//local buffer used in the sliding window protocol
    private int bufSize;
    private String hostName;//host name 
    private int serverPort;//port number of the server
    private fileName;//name of the file to be transfered
    private int N;  //window size
    private int MSS;//maximum segment size
    private int winHead;//pointer to the head of the window
    private int winCur;// pointer to the current byte of the window
    private int winTail;// pointer to the tail of the window
    private int bufHead;//pointer to the head of the buffer
    private int bufTail;// pointer to the tail of the buffer
    private int headerLen = 8;// length of the header(in byte)
    private Semaphore sem = new Semaphore(bufSize);

    public Client(String hostName, int serverPort, String fileName, int N, int MSS)
    {
        this.hostName = hostName;
        this.serverPort = serverPort;
        this.fileName = fileName;
        this.N = N;
        this.MSS = MSS;
        this.bufSize = 2*Math.max(MSS,N);
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
        this.winTail = this.N-1;
    }

    public void start()
    {
        initBuf();
        SlidingWindow sw = new SlidingWindow(buffer, sem);
        sw.start();

        try(FileInputStream fio = new FileInputStream(fileName))
        {
            byte [] temp = new byte[MSS];
            while( read(temp, headerLen, MSS-headerLen) != -1 )
            {
                send(wrapHeader(temp));
            }
        }

    }

    /*
     * Warp the segment with header and return
     */
    public byte[] wrapHeader(byte[] temp)
    {
        return temp;
    }
    
    /*
     * transfer the byte to buffer
     */
    public void send(byte[] temp)
    {
        for(int i=0; i<temp.length; i++)
        {
            sem.acquire();
            buffer[bufTail] = temp[i];
            bufTail = (bufTail + 1) % bufSize;
            sem.release();
        }
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
}
