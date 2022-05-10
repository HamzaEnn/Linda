package linda.test;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import linda.*;
import linda.server.LindaClient;
import linda.server.LindaServeurImpl;
import linda.server.LoadBalancer;
import linda.shm.CentralizedLinda;

public class TestMultiServ2 {

    public static void main(String[] a) throws IOException {

        int nbServ = 6;
        int nbClient = 9;
                
        try {
            CentralizedLinda sharedLindaMemory = new CentralizedLinda();
            // Creation of nbServ servers from 8081 +
            for (int k=1; k<nbServ; k++) {
                String port[] = {String.valueOf(8080+k)};
                LindaServeurImpl.main(port, sharedLindaMemory);
            }

            // Creation of nbClient clients, dispersed randomly in the created servers
            ArrayList<Linda> lindas = new ArrayList<Linda>();
            Random rand = new Random();
            for (int k=0; k<nbClient; k++) {
                int port = 8080+rand.nextInt(nbServ);
                System.out.println("nouveau port client: "+port);
                Linda client = new LindaClient("//localhost:"+port+"/lindaServer");
                lindas.add(client);
            }
            final Socket s = new Socket();
            //final LoadBalancer lb = new LoadBalancer(s);
            //lb.setHP(hosts, ports);
            // final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
                    
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res;
                    System.out.println("(client2) will read:"+motif);
                    res = lindas[3].read(motif);
                    System.out.println("(client2) Resultat:" + res);
                    linda1.debug("(client2)");

                    System.out.println("(client1) will read:"+motif);
                    res = linda1.read(motif);
                    System.out.println("(client1) Resultat:" + res);
                    linda1.debug("(client1)");

                }
            }.start();
                    
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Tuple t1 = new Tuple(4, 5);
                    System.out.println("(client2) write: " + t1);
                    linda1.write(t1);

                    Tuple t11 = new Tuple(4, 5);
                    System.out.println("(client2) write: " + t11);
                    linda1.write(t11);

                    Tuple t2 = new Tuple("hello", 15);
                    System.out.println("(client2) write: " + t2);
                    linda1.write(t2);
                                    
                    linda1.debug("(client2)");

                }
            }.start();


            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Tuple t3 = new Tuple(4, "foo");
                    System.out.println("(client2) write: " + t3);
                    linda2.write(t3);

                    Tuple t4 = new Tuple("hello", 15);
                    System.out.println("(client2) write: " + t4);
                    linda2.write(t4);

                    Tuple t5 = new Tuple("hello", 15);
                    System.out.println("(client2) write: " + t5);
                    linda2.write(t5);
                                    
                    linda2.debug("(client2)");

                }
            }.start();
            
            s.close();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }
}
