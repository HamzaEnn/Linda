package linda.autre.outilTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class Actions {

    // Est ce que les listes précédentes sont déjà initialisées ?
    private static Boolean init = false;

    // Indice ou on s'est arreté dans l'écriture des tuples (dans la liste 'tuples')
    private int indexT = -1;
    // Indice ou on s'est arreté dans les tuples motifs (dans la liste 'motifs')
    private int indexM = -1;

    // Un callback simple pour les tests.
	public Callback cb = new callBack1();

	private static TuplesTest donnees = new TuplesTest();
	private static List<Tuple> tuples = donnees.tuples;
	private static List<Tuple> motifs = donnees.motifs;

	// Initialisation
    public void Initialize(){
    	init = true;
    }

    /**
     * Traduire d'un mot dans le fichier et appeler la méthode voulu
     * @param word : le mot à traduire
     * @param linda : le linda à tester.
     * @param threadnum : le numéro du thread en cours.
     */
	public void processWord(String word, Linda linda, String threadnum) {
		
		if (!init) Initialize();

		try {
		switch (word) {
		case "read" :
		case "take" :
		case "readAll" :
		case "takeAll" :
		case "tryRead" :
		case "tryTake" :
			Method method0 = Linda.class.getDeclaredMethod(word, Tuple.class);
			Tuple in = motifs.get(indexMotif());
			print(threadnum + " : " + word + " " + in);
			method0.invoke(linda, in);
			break;
		case "write" :
			Method method1 = Linda.class.getDeclaredMethod(word, Tuple.class);
			Tuple inW = tuples.get(indexTuple());
			print(threadnum + " : write " + inW);
			method1.invoke(linda, inW);
			break;
		case "eventRegister" :
			Method method2 = Linda.class.getDeclaredMethod(
					word, Linda.eventMode.class, Linda.eventTiming.class, Tuple.class, Callback.class);
			Linda.eventMode mode = (Math.random() < 0.5) ? Linda.eventMode.READ : Linda.eventMode.TAKE;
			Linda.eventTiming timing = (Math.random() < 0.5) ? Linda.eventTiming.FUTURE : Linda.eventTiming.IMMEDIATE;
			Tuple motif = motifs.get(indexMotif());
			print(threadnum + " : eventRegister(" + mode + ", " +timing + ", " + motif + ")");
			method2.invoke(linda, mode, timing, motif, cb);
			break;
		default :
			Integer i = Integer.parseInt(word);
			if (i != null) {
				print(threadnum + " : sleep " + i);
				Thread.sleep(i);
			}
			break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtenir l'indice suivant dans la liste des tuples.
	 * @return indice dans 'tuples'
	 */
	private int indexTuple() {
		indexT = (indexT+1) % (tuples.size() - 1);
		return (indexT);
	}

	/**
	 * Obtenir l'indice suivant dans la liste des motifs.
	 * @return indice dans 'motifs'
	 */
	private int indexMotif() {
		indexM = (indexM+1) % (motifs.size() - 1);
		return (indexM);
	}

	/**
	 * La classe callback pour eventRegister
	 *
	 */
    public class callBack1 implements Callback {
        public void call(Tuple t) {
        	if (Tester.bavare) {
                System.out.println("callback : " + t);
        	}
        }
    }

    /**
     * Afficher le message décrivant la méthode à appeler.
     * @param msg
     */
    public void print(String msg) {
    	if (Tester.bavare)
    		System.out.println(msg);
    }


}