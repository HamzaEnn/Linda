
package linda.test;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class TestEventRegisterFuture {

    private static Linda linda;
    private static Tuple cbmotif;
    private static Tuple cbmotif2;
    
    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("CB got "+t);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
            System.out.println("CB done with "+t+" , on prend bien le premier tuple correspondant ajouté après l'appel à eventRegister, car on a choisi le timing Future.");
        }
    }

    public static void main(String[] a) {
        linda = new linda.shm.CentralizedLinda();
        // linda = new linda.server.LindaClient("//localhost:4000/LindaServer");

        cbmotif = new Tuple(Integer.class, String.class);
        cbmotif2 = new Tuple(Integer.class, Integer.class);
        
        System.out.println("Appel à eventRegister");
        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, cbmotif2, new MyCallback());
        
        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        linda.debug("(2)");
        
        System.out.println("Appel à eventRegister");
        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, cbmotif, new MyCallback());
        
        Tuple res = linda.read(cbmotif);
        System.out.println("(3) read: " + res);
        
        Tuple t4 = new Tuple(8, "correct");
        System.out.println("(4) write: " + t4);
        linda.write(t4);


    }

}
