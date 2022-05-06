package linda.autre.outilTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UnknownFormatConversionException;

import linda.Linda;
import linda.shm.CentralizedLinda;

/*
 *  Class Tester is a class used to test the Linda server based on a text file describing the test.
 */
public class Tester {
	
	public static void main(String[] args) {

		if (args == null) {
			System.out.print("command : Tester [filename]");
			return;
		}
		try {
			Linda linda = new CentralizedLinda();
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.print(args[0] + "not found");
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
