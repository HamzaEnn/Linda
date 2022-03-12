package linda.search.basic;

import linda.*;

public class Main {

    public static void main(String args[]) {
    	/*if (args.length != 2) {
            System.err.println("linda.search.basic.Main search file.");
            return;
    	}*/

    	int length = args.length;
    	String path = args[length-1];
    	
        Manager manager;
        Searcher searcher;
        for (String elt : args) {
            manager = new Manager(new linda.server.LindaClient("//localhost:4000/LindaServer"), path, elt);
            (new Thread(manager)).start();
        }
        for (String elt : args) {
        	searcher = new Searcher(new linda.server.LindaClient("//localhost:4000/LindaServer"));
            (new Thread(searcher)).start();
        }
        
    }
}
