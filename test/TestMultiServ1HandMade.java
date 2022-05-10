package linda.test;

import java.io.IOException;

import linda.*;
import linda.server.LindaClient;
import linda.server.LindaServeurImpl;
import linda.shm.CentralizedLinda;

public class TestMultiServ1HandMade {

    public static void main(String[] a) throws IOException {

        int ports[] = {8081, 8082};
        String args1[] = {String.valueOf(ports[0])};
        String args2[] = {String.valueOf(ports[1])};
                
        CentralizedLinda sharedLindaMemory = new CentralizedLinda();
        LindaServeurImpl.main(args1, sharedLindaMemory);
        LindaServeurImpl.main(args2, sharedLindaMemory);
        final Linda linda1 = new LindaClient("//localhost:8081/LindaServer");
        final Linda linda2 = new LindaClient("//localhost:8082/LindaServer");
        //final Socket s = new Socket();
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
                res = linda2.read(motif);
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
    }
