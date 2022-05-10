package linda.autre.outilTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import linda.Linda;

public class Processes {

	List<Thread> threads;
    File file;

	public Processes(File _file) {
		this.threads = new ArrayList<Thread>();
		this.file = _file;
	}

    public void processFile(Linda linda) throws IOException {
		BufferedReader br
        		= new BufferedReader(new FileReader(file));
		
		String lineStr;
		Actions actions = new Actions();
		while ((lineStr = br.readLine()) != null) {
			List<String> line = strToArray(lineStr);
			this.threads.add(processLine(line, linda, actions));
		}
		br.close();
		
		for (Thread thread : this.threads) {
			thread.start();
		}
		for (Thread thread : this.threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Thread processLine(List<String> line, Linda linda, Actions actions) {

		Iterator<String> iterator = line.iterator();

		return new Thread() {
			public void run() {
				while (iterator.hasNext()) {
					String word = iterator.next();
					try {
						actions.processWord(word, linda, this.getName());

					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		};

	}
    
	
	private List<String> strToArray(String str) {
		String st[] = str.split(" ");
		List<String> liste = new ArrayList<String>();

		liste = Arrays.asList(st);
		return liste;
	}
}
