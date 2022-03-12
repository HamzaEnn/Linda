package linda.test;

import linda.*;

public class ClientsReadTest {

    public static void main(String[] a) {
        
      
        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Linda linda1 = new linda.server.LindaClient("//localhost:4000/LindaServer");
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda1.read(motif);
                    System.out.println("("+j+") Resultat:" + res);
                    linda1.debug("("+j+")");
                }
            }.start();
        }
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Linda linda2 = new linda.server.LindaClient("//localhost:4000/LindaServer");
                
                Tuple t1 = new Tuple(4, 5);
                System.out.println("(1) write: " + t1);
                linda2.write(t1);

                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(2) write: " + t2);
                linda2.write(t2);

                linda2.debug("(0)");

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(3) write: " + t3);
                linda2.write(t3);
                                
                linda2.debug("(0)");

            }
        }.start();
                
    }
}