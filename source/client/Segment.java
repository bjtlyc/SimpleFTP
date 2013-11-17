/*
* Data structure for segment
*/
import java.util.*;
public class Segment
{
    private int seqno;
    private short checksum;
    private int type; // 0-data, 1-ack  21845(85,85) measn data, 43690(170,170) means ack 
    private int status; // 0-unacked     1-acked
    private byte [] seg;

    /* Constructor for segmetn
    */
    public Segment(int seqno, int size, byte[] seg)
    {
        status = 0; 
        this.seg = seg;
        seg[6] = 85;
        seg[7] = 85;
        Helper.int2byte(seqno, this.seg);
        checksum = check(seg);
    }
        
    public Segment(byte[] seg, int size)
    {
        this.seg = Arrays.copyOf(seg,size);
        this.seqno = Helper.byte2int(seg);
        short temp = (short)((int)seg[6] + ((int)seg[7] << 8));
        if(temp == 21845)
            type = 0;
        else if(temp == 43690)
            type = 1;
    }

        /* 
         * Calculate the checksum
         */
        public short check(byte[] a)
        {
            return 0;
        }
        public byte[] get()
        {
            return this.seg;
        }
        public int size()
        {
            return this.seg.length;
        }
        public int type()
        {
            return this.type;
        }
        public int getSeqNo()
        {
            return this.seqno;
        }
}
