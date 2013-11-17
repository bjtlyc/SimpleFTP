/*
 * Class to handle sliding window
 */
import java.util.*;
public class SlidingWindow extends Thread
{
    private Segment [] buffer;//local buffer used in the sliding window protocol
    private DatagramSocket socket = null; 
    private int bufSize;
    private int MSS;
    private int segSize;
    private int bufHead;//pointer to the head of the buffer
    private int bufTail;// pointer to the tail of the buffer
    private Semaphore mutex;
    private Semaphore empty;
    private Semaphore item;

    public SlidingWindow(DatagramSocket socket, Segment[] buffer, Semaphore mutex, Semaphore empty, Semaphore item, int segSize)
    {
        this.socket = socket;
        this.buffer = buffer;
        this.mutex = mutex;
        this.empty = empty;
        this.item = item;
    }

    /* Receive datagram from server, if ack, advance window. 
     * Whenever send a byte, start a timer for it. If overdue, resend the byte
     */
    @Override
    public void run()
    {
        while(running)
        {
            byte[] buf = new byte[segSize];
            // get response
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            // display response
            Segment seg = parse(packet);
            consume(seg);
        }
    }

    public void consume(Segment seg)
    {
        item.acquire();
        mutex.acquire();

        //set the status, if the head of the buffer, remove, else, do nothing. 
        
        // cancel the timer
        mutex.release();
        empty.release();
    }
}
