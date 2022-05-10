package linda.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.util.Random;

public class LoadBalancer extends Thread {

    static String hosts[];// = {"host1", "host2"};
    static int ports[];// = {8081, 8082};
    static int nbHosts = 0;
    static Random rand = new Random();
    Socket s1;

    public LoadBalancer(Socket s) {
        this.s1 = s;
    }

    public void setHP(String h[], int p[]) {
        this.hosts = h;
        this.ports = p;
        this.nbHosts = h.length;
    }

    public void close() throws IOException {
        this.s1.close();
    }

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
        byte[] buff = new byte[1024];
        int nb;

        try {
            int target = this.rand.nextInt(this.nbHosts);
            Socket s2 = new Socket(this.hosts[target], this.ports[target]);

            OutputStream clientOS = s1.getOutputStream();
            InputStream clientIS = s1.getInputStream();
            OutputStream serverOS = s2.getOutputStream();
            InputStream serverIS = s2.getInputStream();

            //client->server
            nb = clientIS.read(buff);
            serverOS.write(nb);
            //server->client
            nb = serverIS.read(buff);
            clientOS.write(nb);

            s1.close();
            s2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
