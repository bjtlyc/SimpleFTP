/*
 * Class to handle sliding window
 */
import java.util.*;
public class SlidingWindow extends Thread
{
    private byte [] buffer;//local buffer used in the sliding window protocol
    private int bufSize;
    private int winHead;//pointer to the head of the window
    private int winCur;// pointer to the current byte of the window
    private int winTail;// pointer to the tail of the window
    private int bufHead;//pointer to the head of the buffer
    private int bufTail;// pointer to the tail of the buffer

    public SlidingWindow(byte[] buffer, Semaphore sem)
    {
        this.buffer = buffer;
        this.sem = sem;
    }

    /* Receive datagram from server, if ack, advance window. 
     * Whenever send a byte, start a timer for it. If overdue, resend the byte
     */
    @Override
    public void run()
    {

    }
}
