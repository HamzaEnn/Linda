package linda.test;

import linda.*;

public class ClientsReadTakeTest {

    public static void main(String[] a) {
               
        new Thread() {
            public void run() {
            	try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        		final Linda linda1 = new linda.server.LindaClient("//localhost:4000/LindaServer");

                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda1.read(motif);
                System.out.println("read Resultat:" + res);
                linda1.debug("read");
            }
        }.start();
        
        for (int i = 1; i <= 4; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            		final Linda linda2 = new linda.server.LindaClient("//localhost:4000/LindaServer");
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda2.take(motif);
                    System.out.println("("+j+") take Resultat:" + res);
                    linda2.debug("("+j+")");
                }
            }.start();
        }
        
        for (int i = 1; i <= 5; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                	try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            		final Linda linda3 = new linda.server.LindaClient("//localhost:4000/LindaServer");
                    Tuple t3 = new Tuple(4, "foo");
                    System.out.println("("+j+") write: " + t3);
                    linda3.write(t3);
                                    
                    linda3.debug("("+j+")");

                }
            }.start();
        }   
  }
}