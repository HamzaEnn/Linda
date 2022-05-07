package linda.test;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

import linda.*;
import linda.server.LindaServeurImpl;
import linda.server.LoadBalancer;
import linda.shm.CentralizedLinda;

public class BasicTest1MultiServ {

    public static void main(String[] a) throws IOException {
                
        try {
            final CentralizedLinda linda = new CentralizedLinda();
            final LindaServeurImpl linda1 = new linda.server.LindaServeurImpl();
            linda1.changeLinda(linda);
            final LindaServeurImpl linda2 = new linda.server.LindaServeurImpl();
            linda2.changeLinda(linda);
            final Socket s = new Socket();
            final LoadBalancer lb = new LoadBalancer(s);
            // final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
                    
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    System.out.println("(1) will take:" );
                    Tuple res;
                    try {

                        System.out.println("(1) will read:");
                        res = linda1.read(motif);
                        System.out.println("(1) Resultat:" + res);
                        linda1.debug("(1)");

                        System.out.println("(2) will read:");
                        res = linda2.read(motif);
                        System.out.println("(2) Resultat:" + res);
                        linda1.debug("(2)");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
                    
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {

                        Tuple t1 = new Tuple(4, 5);
                        System.out.println("(2) write: " + t1);
                        linda1.write(t1);

                        Tuple t11 = new Tuple(4, 5);
                        System.out.println("(2) write: " + t11);
                        linda1.write(t11);

                        Tuple t2 = new Tuple("hello", 15);
                        System.out.println("(2) write: " + t2);
                        linda1.write(t2);
                                        
                        linda1.debug("(2)");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }.start();


            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {

                        Tuple t3 = new Tuple(4, "foo");
                        System.out.println("(2) write: " + t3);
                        linda2.write(t3);

                        Tuple t4 = new Tuple("hello", 15);
                        System.out.println("(2) write: " + t4);
                        linda2.write(t4);

                        Tuple t5 = new Tuple("hello", 15);
                        System.out.println("(2) write: " + t5);
                        linda2.write(t5);
                                        
                        linda2.debug("(2)");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            
            s.close();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }
}
