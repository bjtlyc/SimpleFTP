import java.util.Timer;
import java.util.TimerTask;
 
public class TimerSample {
 
    public static void main(String[] args) {
        //1- Taking an instance of Timer class.
        Timer timer = new Timer("Printer");
 
        //2- Taking an instance of class contains your repeated method.
        MyTask t = new MyTask(timer);
 
 
        //TimerTask is a class implements Runnable interface so
        //You have to override run method with your certain code black
 
        //Second Parameter is the specified the Starting Time for your timer in
        //MilliSeconds or Date
 
        //Third Parameter is the specified the Period between consecutive
        //calling for the method.
 
        timer.schedule(t, 2000);
 
 
    }
}
 
class MyTask extends TimerTask {
    //times member represent calling times.
    private int times = 0;
    private Timer t;
    public MyTask(Timer t)
    {
        this.t = t;
    }
 
    public void run() {
        times++;
        if (times <= 5) {
            System.out.println("I'm alive...");
            t.schedule(new MyTask(t),2000);
        } else {
            System.out.println("Timer stops now...");
 
            //Stop Timer.
 
            //this.cancel();
        }
    }
}
