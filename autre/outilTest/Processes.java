package linda.autre.outilTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import linda.Linda;
import linda.autre.outilTest.MisUseException;
import linda.shm.CentralizedLinda;

public class Processes {

	List<Thread> threads;
    File file;

	public Processes(File _file) {
		this.threads = new ArrayList<Thread>();
		this.file = _file;
	}

    public void processFile(Linda linda) throws IOException, MisUseException {
		BufferedReader br
        		= new BufferedReader(new FileReader(file));
		
		String lineStr;
		while ((lineStr = br.readLine()) != null) {
			List<String> line = strToArray(lineStr);
			this.threads.add(processLine(line, linda));
		}
		
		for (Thread thread : this.threads) {
			thread.start();
		}
	}
	
	private Thread processLine(List<String> line, Linda linda) throws MisUseException {
		//int nbBoucles;
		Iterator<String> iterator = line.iterator();
		Thread thread;
		Method method;
/*
		try {
			nbBoucles = Integer.parseInt(iterator.next());
		} catch (Exception e) {
			throw new MisUseException();
		}
*/
		thread = new Thread() {
			public void run() {
				while (iterator.hasNext()) {
					String word = iterator.next();
					try {
						method = CentralizedLinda.class.getMethod(word);
					} catch (NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
					List<Object> listPar = Actions.getParameters(word);
					method.invoke(linda, listPar);
					
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
