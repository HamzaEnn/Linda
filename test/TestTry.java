package linda.test;

import linda.*;


public class TestTry {

	public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //              final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");

        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.tryTake(motif);
                Tuple res2 = linda.tryRead(motif);
                if (res == null && res2 == null) {
                	System.out.println("(0) Rien trouvé, non bloquant");
                } else {
                	System.out.println("(0) oups");
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                System.out.println("(1) write: " + t1);
                linda.write(t1);

                Tuple t2 = new Tuple(15, "hello");
                System.out.println("(1) write: " + t2);
                linda.write(t2);

                linda.debug("(1)");

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(1) write: " + t3);
                linda.write(t3);
                                
                linda.debug("(1)");
                
                Tuple t4 = new Tuple(8, 4);
                System.out.println("(1) write: " + t4);
                linda.write(t4);
                
                linda.debug("(1)");
                
                Tuple t5 = new Tuple(1, "world");
                System.out.println("(1) write: " + t5);
                linda.write(t5);
                
                linda.debug("(1)");

            }
        }.start();
        
        for (int i = 1; i <= 4; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda.tryTake(motif);
                    linda.debug("("+(j+1)+")");
                    if (res == null) {
                    	System.out.println("("+(j+1)+") Rien trouvé, non bloquant.");
                    } else {
                    	System.out.println("("+(j+1)+") Resultat :" + res);
                    }
                }
            }.start();
        }
        
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple motif2 = new Tuple(Integer.class, Integer.class);
                Tuple res = linda.tryRead(motif);
                Tuple res2 = linda.tryRead(motif2);
                if (res == null) {
                	System.out.println("(6) Rien trouvé, non bloquant");
                } else {
                	System.out.println("oups");
                }
                System.out.println("(6) Resultat :" + res2);
            }
        }.start();
        
    }
}
