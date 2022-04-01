package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.autre.EspaceTuples;
import linda.autre.Synchronization;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	// Espace principal de tuples
	private EspaceTuples espace;

	/*
	// Verou pour les methodes sur l'espace.
	private ReentrantLock moniteur;
	// Verou pour la methode read en particulier (plus d'infos dans les commentaires dans la methode read)
	private ReentrantLock moniteurRead;
	
	// Condition de read/tryread/readAll
	private Condition canRead;
	// Condition de write/take/tryTake/takeAll
	private Condition canWrite;
	
	private int nbDemandesWrite = 0;
	private int nbDemandesRead = 0;
	private int nbRead = 0;
	private boolean writing = false;
	*/

	
	// objet offrant des methodes d'acces pour les different methodes read/write/take/...
	private Synchronization sync;

	public CentralizedLinda() {
		espace = new EspaceTuples();
		sync = new Synchronization();
	}



	public Tuple tryTake(Tuple template) {

		sync.beginModify();

		Tuple res = espace.rechercher(template, true);

		sync.endModify();

		return res;
	}

	public Tuple tryRead(Tuple template) {

		sync.beginRead();

		Tuple res = espace.rechercher(template, false);

		sync.endRead();

		return res;

	}

	public void write(Tuple t) {

		sync.beginModify();

		espace.add(t.deepclone());
		

		sync.endModify();
		sync.wakeEventReg(t, espace);

		sync.wakeConditions(t);

	}

	public Tuple take(Tuple template) {
		Tuple res = tryTake(template);
		if (res == null)
			res = takeInFuture(template);
		return res;
	}
	
	private Tuple takeInFuture(Tuple template) {
		boolean took = false;
		Tuple res = null;
		while (!took) {
			//sleep until a tuple matching the template shows up
			res = sync.sleepTake(template);

			sync.beginModify();

			took = this.espace.remove(res);

			sync.endModify();
		}

		return res;
	}

	public Tuple read(Tuple template) {

		Tuple res = tryRead(template);
		if (res == null) {
			res = sync.getTupleWhenExists(template);
		}
		
		return res;
	}

	public Collection<Tuple> takeAll(Tuple template) {

		ArrayList<Tuple> res = new ArrayList<Tuple>();
		sync.beginModify();

		for (Tuple t : this.espace.getAll()) {
			if (t.matches(template)) {
				espace.remove(t);
				res.add(t);
			}
		}

		sync.endModify();

		return res;

	}

	public Collection<Tuple> readAll(Tuple template) {

		ArrayList<Tuple> res = new ArrayList<Tuple>();

		sync.beginRead();

		for (Tuple t : this.espace.getAll()) {
			if (t.matches(template)) {
				res.add(t.deepclone());
			}
		}

		sync.endRead();

		return res;
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		Tuple res = null;

		if (timing == eventTiming.IMMEDIATE) {
			if (mode == eventMode.READ) {
				res = this.tryRead(template);
			} else if (mode == eventMode.TAKE) {
				res = this.tryTake(template);
			} else return;
		}

		if (res == null) {
			sync.addEventAlarm(template, callback, mode);
		} else {
			callback.call(res);
		}
		
	}

	public void debug(String prefix) {
		System.out.println(prefix);
	}

}
