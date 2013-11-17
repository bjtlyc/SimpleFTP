import java.util.*;
public class Server extends Thread
{
    private int serverPort;// port number of the server
    private String fileName;//name of the file will be written
    private float p; // package loss possibility
    private DatagramSocket socket = null;
    private int segSize = 2048;

    public Server(int serverPort, String fileName, float p)
    {
        this.serverPort = serverPort;
        this.fileName = fileName;
        this.p = p;
        this.socket = new DatagramSocket(serverPort);
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
            while(running)
            {
                byte [] seg = new byte[segSize];
                DatagramPacket packet = new DatagramPacket(seg, seg.length);
                socket.receive(packet);

                int value = check(seg);
                if(value == 0)
                {
                    byte [] response = getResponse(value);
                    byte [] data = process();
                    fio.write(data);
                }
                else if(value == 1)
                    byte [] response = getResponse(value);
                else 
                {
                    byte [] response = getResponse(value);
                    running = false;
                }

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(response);
                socket.send(packet);
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
    public int check(byte[] seg)
    {
        return 0;
    }

    /*
     * Construct the response segment
     */
    public byte[] getResponse(int value)
    {

    }

    public static void main(String args[])
    {
        if(args.length != 3 )
        {
            System.err.println("Invalid Parameter");
            System.exit(1);
        }
        Server s = new Server(args[0], args[1], args[2]);
        s.start();
    }
}
