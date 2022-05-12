package linda.test;

import linda.*;

public class TestAmeliorationTake {

    public static void main(String[] a) {
                
        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
        
		 Thread t1 = new Thread() {
		     public void run() {
		         Tuple motif = new Tuple(Integer.class, String.class);
		         Tuple res = linda.take(motif);
		         System.out.println("(1) Resultat:" + res);
		     }
		 };

		 Thread t2 = new Thread() {
		     public void run() {
		         try {
		             Thread.sleep(100);
		         } catch (InterruptedException e) {
		             e.printStackTrace();
		         }
		
		         Tuple t1 = new Tuple(true, 4);
		         System.out.println("writing 10000 fois : " + t1);
					 for (int i = 0 ; i < 10000 ; i++) {
					     linda.write(t1);
					 }
					
					 Tuple t2 = new Tuple(5, "five");
					 System.out.println("writing  : " + t2);
					         linda.write(t2);
					
		     }
		 };
              
		long start = System.currentTimeMillis();
		try {
			t1.start();
			t2.start();
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		
		System.out.println("temps écoulé : " + timeElapsed + "ms");
    }
}
