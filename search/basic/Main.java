package linda.search.basic;

import linda.*;

public class Main {

    public static void main(String args[]) {
    	/*if (args.length != 2) {
            System.err.println("linda.search.basic.Main search file.");
            return;
    	}*/
    	String[] motsAChercher = {"agneau" , "chien", "chat", "humain"};
    	int nbChercheurs = 5;
    	int nbManagers = 4;
    	String currentDir = System.getProperty("user.dir");
    	
        
        for (int i = 0; i < nbManagers ; i++) {
        	Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
            Manager manager = new Manager(linda, currentDir + "\\texte.txt", motsAChercher[i]);
            (new Thread(manager)).start();
        }
        for (int i = 0; i < nbChercheurs ; i++) {
        	Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
        	Searcher searcher = new Searcher(linda);
            (new Thread(searcher)).start();
        }
        
        /*
         * Bloqué dans cette application :
         * 
         * Erreurs dans la lecture d'un fichier un peu grand.
         * Echec de la terminaison après avoir trouvé le bon mot.
         * 
         */
    }
}
