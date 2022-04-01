package linda.test;

import linda.*;

public class TestFamine {

    public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //              final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
               
        new Thread() {
            public void run() {
            	try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.read(motif);
                System.out.println("read Resultat:" + res);
            }
        }.start();
        
        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda.take(motif);
                    System.out.println("("+j+") take Resultat:" + res);
                    linda.debug("("+j+")");
                }
            }.start();
        }

        for (int i = 1; i <= 4; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                	try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Tuple t3 = new Tuple(4, "foo");
                    linda.write(t3);
                    System.out.println("("+j+") write: " + t3);
                                    
                    linda.debug("("+j+")");

                }
            }.start();
        }
                
        
                
        System.out.println("Ici, on voit qu'il y a un certain ordre de priorité : write > take > read");
        System.out.println("Si on remplace les boucles for par des while(true) et qu'on a des écritures et des take sans arrêt, les opérations read seront en famine");
    }
}
