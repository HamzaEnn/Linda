package linda.autre.outilTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import linda.Tuple;

public class TuplesTest {

	// Tuples à utiliser.
		private static Tuple t1 = new Tuple(1, 2);
			private static Tuple m1 = new Tuple(Integer.class, 2);
		private static Tuple t2 = new Tuple("foo", "fa", true);
			private static Tuple m2 = new Tuple(String.class, "fa", Boolean.class);
		private static Tuple t3 = new Tuple(22, "Toulouse", "Enseeiht");
			private static Tuple m3 = new Tuple(Integer.class, String.class, String.class);
		private static Tuple t4 = new Tuple("two", 2);
			private static Tuple m4 = new Tuple(String.class, 2);
		private static Tuple t5 = new Tuple(true, "bool");
			private static Tuple m5 = new Tuple(Boolean.class, String.class);
		private static Tuple t6 = new Tuple(22);
			private static Tuple m6 = new Tuple(Integer.class);
		private static Tuple t7 = new Tuple("foo", 4, true);
			private static Tuple m7 = new Tuple(String.class, 4, Boolean.class);
		private static Tuple t8 = new Tuple(22, true, "Enseeiht");
			private static Tuple m8 = new Tuple(Integer.class, true, String.class);
		private static Tuple t9 = new Tuple(19, 2);
			private static Tuple m9 = new Tuple(19, 2);
		private static Tuple t10 = new Tuple(true, false, true);
			private static Tuple m10 = new Tuple(true, Boolean.class, Boolean.class);
		private static Tuple t11 = new Tuple("ecole", "Toulouse", "Enseeiht");
			private static Tuple m11 = new Tuple(String.class, String.class, String.class);

		// Tuples pour write
	    public static List<Tuple> tuples = new ArrayList<Tuple>();
	    // Tuples pour l'accés (motifs)
	    public static  List<Tuple> motifs = new ArrayList<Tuple>();
	    
	    public TuplesTest() {
	    	Collections.addAll(tuples, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
	    	Collections.addAll(motifs, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11);
	    }
}
