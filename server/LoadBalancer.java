package linda.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class LoadBalancer extends Thread{
    Socket client = null;
    int nbHosts = 0;
    static int[] hosts;
    int nbPorts = 0;
    static int[] ports;

    public static void main (String args[]) {
        try {
            ServerSocket ss = new ServerSocket(8080);
            while (true) {
                Socket s = ss.accept();
                LoadBalancer lb = new LoadBalancer(s);
                lb.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        int target = Random.nextInt(this.nbHosts);
        Socket server = new Socket(this.hosts[target], this.ports[target]);
        OutputStream clientOS = client.getOutputStream();
        InputStream clientIS = client.getInputStream();
        OutputStream serverOS = server.getOutputStream();
        InputStream serverIS = server.getInputStream();
        byte[] buff = new byte[1024];
        int nb = clientIS.read(buff);
        serverOS.write(nb);
        nb = serverIS.read(buff);
        clientOS.write(nb);
        server.close();
        client.close();
    }
}
