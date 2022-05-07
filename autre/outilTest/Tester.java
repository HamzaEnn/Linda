package linda.autre.outilTest;

import java.io.File;

import linda.Linda;
import linda.shm.CentralizedLinda;

/*
 *  Class Tester is a class used to test the Linda server based on a text file describing the test.
 */
public class Tester {
	
	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.print("Utilisation\n commande : Tester [filename]");
			return;
		}
		try {
			Linda linda = new CentralizedLinda();
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.print(args[0] + " not found");
				return;
			} else {
				Processes processes = new Processes(file);
				processes.processFile(linda);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
