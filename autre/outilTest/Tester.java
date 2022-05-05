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

/*
 *  Class Tester is a class used to test the Linda server based on a text file describing the test.
 */
public class Tester {

	List<Thread> threads;
	File file;

	private Tester(File _file) {
		this.threads = new ArrayList<Thread>();
		this.file = _file;
	}

	private void processFile() throws IOException {
		BufferedReader br
        		= new BufferedReader(new FileReader(file));
		
		String lineStr;
		while ((lineStr = br.readLine()) != null) {

			List<String> line = strToArray(lineStr);
			this.threads.add(processLine(line));
		}
		
		for (Thread thread : this.threads) {
			thread.start();
		}
	}
	
	private List<String> strToArray(String str) {
		String st[] = str.split(" ");
		List<String> liste = new ArrayList<String>();

		liste = Arrays.asList(st);
		return liste;
	}
	
	private Thread processLine(List<String> line) throws MisUseException {
		int nbBoucles;
		Iterator<String> iterator = line.iterator();
		Thread thread;

		try {
			nbBoucles = Integer.parseInt(iterator.next());
		} catch (Exception e) {
			throw new MisUseException();
		}

		while (iterator.hasNext()) {
			Actions.processWord(iterator.next(), iterator);
		}
	}
	
	
	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
