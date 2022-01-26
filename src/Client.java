import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
    public static void main(String[] args) throws Exception
    {
        socket = new Socket("localhost", 8888);
        sysOut = new PrintWriter(System.out, true);
        sysIn = new BufferedReader(new InputStreamReader(System.in));
        sockOut = new PrintWriter(socket.getOutputStream(), true);
        sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        msgOut = "";
        msgIn = "";
        con=true;

        sysOut.println("Connected with server");

        while (con)
        {
             while(sockIn.ready())
            {
                sysOut.println(sockIn.readLine());
            }

            while(sysIn.ready())
            {
                msgOut=sysIn.readLine();

                if(msgOut.equals("exit"))
                {
                    con=false;
                    break;
                }
                else
                {
                    sockOut.println(msgOut);
                }
            }
        }
    }

    static boolean con;
    static Socket socket;
    static String msgOut;
    static String msgIn;
    static PrintWriter sysOut;
    static BufferedReader sysIn;
    static PrintWriter sockOut;
    static BufferedReader sockIn;
}
