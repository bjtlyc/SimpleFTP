public class Server
{
    private int serverPort;// port number of the server
    private String fileName;//name of the file will be written
    private float p; // package loss possibility

    public Server(int serverPort, String fileName, float p)
    {
        this.serverPort = serverPort;
        this.fileName = fileName;
        this.p = p;
    }

    /*
     * Always waiting to receive packets from client, when received packects, determine whether 
     * this packets is good or bad, if good, send back a ACK; if bad, drop it. Maintain the 
     * sliding window.
     */
    public void run()
    {
        socket = new DatagramSocket(serverPort);
        try()
        {

        }
    }

    public static void main(String args[])
    {
        if(args.length != 3 )
        {
            System.err.println("Invalid Parameter");
            System.exit(1);
        }
        Server s = new Server(args[0], args[1], args[2]);
        s.run();
    }
}
