
package linda.test;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class TestImmediateFuture {

    private static Linda linda;
    private static Tuple cbmotif;
    
    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            System.out.println("CB Immediate got "+t);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
            System.out.println("CB Immediate done with "+t);
        }
    }
    
    private static class MyCallback2 implements Callback {
        public void call(Tuple t) {
            System.out.println("CB Future got "+t);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
            System.out.println("CB Future done with "+t);
        }
    }

    public static void main(String[] a) {
        linda = new linda.shm.CentralizedLinda();
        // linda = new linda.server.LindaClient("//localhost:4000/LindaServer");

        cbmotif = new Tuple(Integer.class, String.class);
        
        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        linda.debug("(2)");
        
        System.out.println("Appel à eventRegister");
        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, cbmotif, new MyCallback2());
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, cbmotif, new MyCallback());
        
        Tuple t3 = new Tuple(8, "correct");
        System.out.println("(3) write: " + t3);
        linda.write(t3);
        
        Tuple t4 = new Tuple(14, "super");
        System.out.println("(4) write: " + t4);
        linda.write(t4);

        System.out.println("Les deux eventRegister renvoie des tuples différents, on s'assure ainsi que FUTURE et IMMEDIATE font un take qui fonctionne.");
        System.out.println("Cela permet aussi de montrer le fonctionnement des priorités");
    }

}
