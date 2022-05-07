package linda.autre.outilTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class Actions {

	private static Tuple t1 = new Tuple(1, 2);
		private static Tuple m1 = new Tuple(Integer.class, 2);
	private static Tuple t2 = new Tuple("foo", "fa", true);
		private static Tuple m2 = new Tuple(String.class, "fa", Boolean.class);
	private static Tuple t3 = new Tuple(22, "Toulouse", "Enseeiht");
		private static Tuple m3 = new Tuple(Integer.class, String.class, String.class);

    private static List<Tuple> tuples = new ArrayList<Tuple>();
    private static  List<Tuple> motifs = new ArrayList<Tuple>();
    private static Boolean init = false;

    private static int indexT = -1;
    private static int indexM = -1;
	public Callback cb = new callBack1();

    public void Initialize(){
    	Collections.addAll(tuples, t1, t2, t3);
    	Collections.addAll(motifs, m1, m2, m3);
    	init = true;
    }

	public void processWord(String word, Linda linda, String threadnum) {
		
		if (!init) Initialize();

		try {
		switch (word) {
		case "read" :
		case "take" :
		case "readAll" :
		case "takeAll" :
			Method method0 = Linda.class.getDeclaredMethod(word, Tuple.class);
			Tuple in = motifs.get(indexMotif());
			System.out.println(threadnum + " : " + word + " " + in);
			method0.invoke(linda, in);
			break;
		case "write" :
			Method method1 = Linda.class.getDeclaredMethod(word, Tuple.class);
			Tuple inW = tuples.get(indexTuple());
			System.out.println(threadnum + " : write " + inW);
			method1.invoke(linda, inW);
			break;
		case "eventRegister" :
			Method method2 = Linda.class.getDeclaredMethod(
					word, Linda.eventMode.class, Linda.eventTiming.class, Tuple.class, Callback.class);
			Linda.eventMode mode = (Math.random() < 0.5) ? Linda.eventMode.READ : Linda.eventMode.TAKE;
			Linda.eventTiming timing = (Math.random() < 0.5) ? Linda.eventTiming.FUTURE : Linda.eventTiming.IMMEDIATE;
			Tuple motif = motifs.get(indexMotif());
			System.out.println(threadnum + " : eventRegister(" + mode + ", " +timing + ", " + motif + ")");
			//method2.invoke(linda, mode, timing, motif, cb);
			break;
		default :
			Integer i = Integer.parseInt(word);
			if (i != null) {
				System.out.println(threadnum + " : sleep " + i);
				Thread.sleep(i);
			}
			break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int indexTuple() {
		indexT = (indexT+1) % (tuples.size() - 1);
		return (indexT);
	}
	private int indexMotif() {
		indexM = (indexM+1) % (motifs.size() - 1);
		return (indexM);
	}

    public class callBack1 implements Callback {
        public void call(Tuple t) {
            System.out.println("callback : " + t);
        }
    }


}