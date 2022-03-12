package linda.test;

import java.util.Collection;

import linda.*;


public class TestTakeAll {

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
                Collection<Tuple> res = linda.takeAll(motif);
                for (Tuple r : res) {
                	System.out.println("(0) Resultat:" + r);
                }
                if (res.size() == 0) {
                	System.out.println("(0) Rien trouvé.");
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
        
        for (int i = 1; i <= 2; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Collection<Tuple> res = linda.takeAll(motif);
                    int indice = 0;
                    for (Tuple r : res) {
                    	System.out.println("("+(j+1)+") Resultat " + indice + ":" + r);
                    	indice++;
                    }
                    linda.debug("("+(j+1)+")");
                    if (res.size() == 0) {
                    	System.out.println("("+(j+1)+") Rien trouvé, le takeAll précédent a tout prit.");
                    }
                }
            }.start();
        }
        
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, Integer.class);
                Collection<Tuple> res = linda.takeAll(motif);
                int indice = 0;
                for (Tuple r : res) {
                	System.out.println("(4) Resultat " + indice + ":" + r);
                	indice++;
                }
                linda.debug("(4)");
                if (res.size() == 0) {
                	System.out.println("(4) Rien trouvé, oups.");
                }
            }
        }.start();
                
        new Thread() {  
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Object.class, Object.class);
                Collection<Tuple> res = linda.readAll(motif);
                int indice = 0;
                for (Tuple r : res) {
                	System.out.println("(5) Resultat " + indice + ":" + r);
                	indice++;
                }
                linda.debug("(5)");
                if (res.size() == 0) {
                	System.out.println("(5) L'espace est vide, tout s'est bien passé.");
                }
            }
        }.start();
    }
}
