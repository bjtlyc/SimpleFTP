/*
* Data structure for segment
*/
import java.util.*;
public class Segment
{
    private int seqno;
    private int headerLen = 8;
    private short checksum;
    private int type; // 0-data, 1-ack  21845(85,85) measn data, 43690(170,170) means ack 
    private int status; // 0-unacked     1-acked
    private byte [] seg;

    /* Constructor for segmetn
    */
    public Segment(int seqno, int size, byte[] seg)
    {
        status = 0; 
        this.seg = Arrays.copyOf(seg, size+headerLen);
        this.seqno = seqno;
        this.type = 0;
        this.checksum = check(seg);
        seg[6] = 0x55;
        seg[7] = 0x55;
        Helper.int2byte(seqno, this.seg);
    }
        
    /*
     * Constructor for response segment
     */
    public Segment(byte[] seg, int size)
    {
        this.seg = Arrays.copyOf(seg,size);
        this.seqno = Helper.byte2int(seg);
        //short temp = (short)((int)seg[6] + ((int)seg[7] << 8));
        //System.out.println(seg[6]+" "+seg[7]);
        if(seg[6] == 0x55 && seg[7] == 0x55)
            this.type = 0;
        else if(seg[6] == (byte)0xAA && seg[7] == (byte)0xAA)
            this.type = 1;
    }

    /*
     * Construct an ACK with sequence no seq
     */
    public Segment(int seq, int size)
    {
        this.seg = new byte[size];
        this.seqno = seq;
        Helper.int2byte(this.seqno, this.seg);
        seg[6] = (byte)0xAA;
        seg[7] = (byte)0xAA;
        checksum = check(seg);
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
        public int getDataSize()
        {
            return this.seg.length - headerLen;
        }
        public byte[] getData()
        {
            return Arrays.copyOfRange(this.seg, headerLen, this.seg.length);
        }
}
