package linda.test;

import linda.*;

public class ClientsTryTakeTest {

    public static void main(String[] a) {
               
        new Thread() {
            public void run() {
            	try {
                    Thread.sleep(15);
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
        
  
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        		final Linda linda2 = new linda.server.LindaClient("//localhost:4000/LindaServer");

                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda2.tryTake(motif);
                System.out.println("(1) Resultat:" + res);
                linda2.debug("(1)");
            }
        }.start();
        
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        		final Linda linda3 = new linda.server.LindaClient("//localhost:4000/LindaServer");

                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda3.tryTake(motif);
                System.out.println("(2) Resultat:" + res);
                linda3.debug("(2)");
            }
        }.start();
        
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        		final Linda linda4 = new linda.server.LindaClient("//localhost:4000/LindaServer");

                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda4.tryTake(motif);
                System.out.println("(3) Resultat:" + res);
                linda4.debug("(3)");
            }
        }.start();
        
        new Thread() {  
            public void run() {
            	try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        		final Linda linda5 = new linda.server.LindaClient("//localhost:4000/LindaServer");
                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(4) write: " + t3);
                linda5.write(t3);                        
                linda5.debug("(4)");

            }
        }.start();
      
  }
}