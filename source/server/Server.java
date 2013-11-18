import java.util.*;
import java.net.*;
import java.io.*;
public class Server extends Thread
{
    private int serverPort;// port number of the server
    private String fileName;//name of the file will be written
    private float p; // package loss possibility
    private DatagramSocket socket = null;
    private int segSize = 1024;
    private Random r;

    public Server(int serverPort, String fileName, float p)
    {
        this.serverPort = serverPort;
        this.fileName = fileName;
        this.p = p;
        this.r = new Random(100);
        try{
            this.socket = new DatagramSocket(serverPort);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /*
     * Always waiting to receive packets from client, when received packects, determine whether 
     * this packets is good or bad, if good, send back a ACK; if bad, drop it. Maintain the 
     * sliding window.
     */
    @Override
    public void run()
    {
        boolean running = true;
        try(FileOutputStream fio = new FileOutputStream(fileName))
        {
            boolean first = true;
            int seq = 0;
            while(running)
            {
                byte [] buf = new byte[segSize];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress hostAddr = packet.getAddress();
                int port = packet.getPort();

                Segment seg = new Segment(packet.getData(), packet.getLength());
                int value = check(seg);
                float randomNum = r.nextFloat();

                if(value == 0 && randomNum > p)
                {
                    System.out.println("Get "+seg.getSeqNo()+" Waiting "+seq);
                    if( first || seg.getSeqNo() == seq )
                    {
                        seq = seg.getSeqNo() + seg.getDataSize();
                        first = false;
                        //System.out.println(Arrays.toString(Arrays.copyOfRange(seg.get(),8,seg.size())));
                        fio.write(seg.getData());
                    }
                    else 
                        seq = seq;

                    Segment response = new Segment(seq, segSize);
                    packet = new DatagramPacket(response.get(), response.size(), hostAddr, port);
                    socket.send(packet);
                }
                else if( randomNum <= p)
                {
                    System.out.println("Packet loss, sequence number r = "+seg.getSeqNo());
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /* 
     * Check whether the receiving segment is correct, or the last segment. 
     * return 0 if is a valid packet, 1 if wrong, else if the last packet
     */
    public int check(Segment seg)
    {
        return 0;
    }

    public static void main(String args[])
    {
        if(args.length != 3 )
        {
            System.err.println("Invalid Parameter");
            System.exit(1);
        }
        float p = Float.valueOf(args[2]);
        if( p < 0 || p > 1)
        {
            System.err.println("p must be less or equal than 1 and larger or equal than 0");
            System.exit(1);
        }

        Server s = new Server(Integer.valueOf(args[0]), args[1], Float.valueOf(args[2]));
        s.start();
    }
}
