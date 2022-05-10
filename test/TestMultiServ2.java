package linda.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import linda.*;
import linda.server.LindaClient;
import linda.server.LindaServeurImpl;
import linda.shm.CentralizedLinda;

public class TestMultiServ2 {

    public static void main(String[] a) throws IOException {

        int nbServ = 6;
        int nbClient = 9;
                
        CentralizedLinda sharedLindaMemory = new CentralizedLinda();
        // Creation of nbServ servers from 8081 +
        for (int k=0; k<nbServ; k++) {
            String port[] = {String.valueOf(8081+k)};
            LindaServeurImpl.main(port, sharedLindaMemory);
        }

        // Creation of nbClient clients, dispersed randomly in the created servers
        ArrayList<Linda> lindas = new ArrayList<Linda>();
        Random rand = new Random();
        for (int k=0; k<nbClient; k++) {
            int portServer = 8081+rand.nextInt(nbServ);
            System.out.println("nouveau client au port: "+portServer);
            Linda client = new LindaClient("//localhost:"+portServer+"/LindaServer");
            lindas.add(client);
        }
                
        // Reading thread
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res;

                //Un client est choisi au hasard pour faire cette action
                int clientNB = rand.nextInt(nbClient);
                System.out.println("(client"+clientNB+") will read:"+motif);
                res = lindas.get(clientNB).read(motif);
                System.out.println("(client"+clientNB+") Resultat:" + res);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                System.out.println("(client"+clientNB+") will read:"+motif);
                res = lindas.get(clientNB).read(motif);
                System.out.println("(client"+clientNB+") Resultat:" + res);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                motif = new Tuple(Integer.class, Integer.class);
                System.out.println("(client"+clientNB+") will read:"+motif);
                res = lindas.get(clientNB).read(motif);
                System.out.println("(client"+clientNB+") Resultat:" + res);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                motif = new Tuple(String.class, String.class);
                System.out.println("(client"+clientNB+") will read:"+motif);
                res = lindas.get(clientNB).read(motif);
                System.out.println("(client"+clientNB+") Resultat:" + res);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

            }
        }.start();
                
        // Writing thread #1
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Un client est choisi au hasard pour faire cette action
                int clientNB = rand.nextInt(nbClient);
                Tuple t1 = new Tuple(4, 5);
                System.out.println("(client"+clientNB+") write: " + t1);
                lindas.get(clientNB).write(t1);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                Tuple t11 = new Tuple(1, 8);
                System.out.println("(client"+clientNB+") write: " + t11);
                lindas.get(clientNB).write(t11);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                Tuple t2 = new Tuple("hello", 15);
                System.out.println("(client"+clientNB+") write: " + t2);
                lindas.get(clientNB).write(t2);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

            }
        }.start();

        // Writing thread #2
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Un client est choisi au hasard pour faire cette action
                int clientNB = rand.nextInt(nbClient);
                Tuple t3 = new Tuple(18, "foo");
                System.out.println("(client"+clientNB+") write: " + t3);
                lindas.get(clientNB).write(t3);
                Tuple t4 = new Tuple("hola", 1523);
                System.out.println("(client"+clientNB+") write: " + t4);
                lindas.get(clientNB).write(t4);
                Tuple t5 = new Tuple("bonjour", 195);
                System.out.println("(client"+clientNB+") write: " + t5);
                lindas.get(clientNB).write(t5);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

                //Un client est choisi au hasard pour faire cette action
                clientNB = rand.nextInt(nbClient);
                Tuple t6 = new Tuple(148, "si senor");
                System.out.println("(client"+clientNB+") write: " + t6);
                lindas.get(clientNB).write(t6);
                Tuple t7 = new Tuple("toto", "titeuf");
                System.out.println("(client"+clientNB+") write: " + t7);
                lindas.get(clientNB).write(t7);
                Tuple t8 = new Tuple("pinpon", "17");
                System.out.println("(client"+clientNB+") write: " + t8);
                lindas.get(clientNB).write(t8);
                //lindas.get(clientNB).debug("(client"+clientNB+")");

            }
        }.start();
    }
}
