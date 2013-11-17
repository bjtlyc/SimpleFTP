import java.util.*;
public class Helper
{
    public static byte[] int2byte(int i, byte[] b)
    {
        b[0] = (byte)(i >> 24);
        b[1] = (byte)(i >> 16);
        b[2] = (byte)(i >> 8);
        b[3] = (byte)(i);
        return b;
    }
    public static byte[] short2byte(short i)
    {
        byte[] b = new byte[2];
        b[0] = (byte)(i >> 8);
        b[1] = (byte)(i);
        return b;
    }

    public static int byte2int(byte[] b)
    {
        int a = 0;
        a = a | ((int)b[3]);
        a = a | ((int)b[2] << 8);
        a = a | ((int)b[1] << 16);
        a = a | ((int)b[0] << 24);
        return a;
    }
    public static short byte2short(byte[] b)
    {
        short a = (short)((int)b[1] + ((int)b[0] << 8));
        return a;
    }
    /*
    public static void main(String args[])
    {
        byte [] b = Helper.int2byte(256);
        byte [] c = Helper.short2byte((short)256);
        System.out.println(Arrays.toString(b));
        System.out.println(Helper.byte2int(b));
        System.out.println(Helper.byte2short(c));
    }
    */
}
