package linda.autre.outilTest;

import java.io.File;
import java.io.IOException;

import linda.Linda;
import linda.server.LindaClient;
import linda.server.LindaServeurImpl;
import linda.shm.CentralizedLinda;

/**
 * Class Tester is a class used to test the Linda server based on a text file describing the test.
 *
 */
public class Tester {
	
	public static boolean bavare = false;
	
	public static void main(String[] args) {

		/*
		CentralizedLinda centrLinda = new CentralizedLinda();
		LindaServeurImpl.main(new String[0], centrLinda);
		*/

		if (args.length == 0) {
			System.out.println("Test de tous les fichiers ...");
			System.out.println("note : Pour tester un seul fichier :");
			System.out.println("		Tester [filename] [y|n](bavare ou pas)");
			System.out.println("");

			File dir = new File("src/linda/autre/outilTest/tests");
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File testFile : directoryListing) {
					try {
						singleTest(testFile);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}else {
				System.out.print("Aucun fichier test n'est trouvé!");
			}
		} else {
			try {
				File file = new File(args[0]);
				if (args.length == 2) {
					bavare = (args[1].equals("y")) ? true : false;
				}
				singleTest(file);
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void singleTest(File file) throws InterruptedException {
        //final Linda linda = new LindaClient("//localhost:4000/LindaServer");
		Linda linda = new CentralizedLinda(); 
		if (!file.exists()) {
			System.out.print(file.getName() + " not found");
			return;
		} else {
			Processes processes = new Processes(file);
			Thread processThread = new Thread() {
				public void run() {
					try {
						processes.processFile(linda);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			System.out.print("Test de : " + file.getName());
			if (bavare) System.out.println();
			
			long start = System.currentTimeMillis();
			processThread.start();
			processThread.join();
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;
			
			System.out.println("	temps mis : " + timeElapsed);
		}
	}

}
