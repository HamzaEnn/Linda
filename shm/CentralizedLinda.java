package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.autre.EspaceTuples;
import linda.autre.Synchronization;


/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	// Espace principal de tuples
	private EspaceTuples espace;

	// objet offrant des methodes d'acces pour les different methodes read/write/take/...
	private Synchronization sync;

	// Constructeur du centralised Linda
	public CentralizedLinda() {
		espace = new EspaceTuples();
		sync = new Synchronization();
	}

	public void write(Tuple t) {

		// Demander l'acces pour écrire(modifier) dans l'espace de tuple
		sync.beginModify();

		//Ajout d'une copie du tuple dans l'espace
		espace.add(t.deepclone());

		// Donner l'acces à l'opération suivante dans la file d'attente FIFO
		sync.endModify();

		//Révéiller les callbacks en attente d'un tuple correspondant
		//et les conditions correspondantes à des lectures(read) et des extractions(take) en attentes
		sync.wakeUp(t, espace);


	}

	public Tuple take(Tuple template) {
		// Essayer de prendre un tuple correspondant
		// et puis on révéille les opérations dans la file FIFO
		Tuple res = tryTake(template);

		// On trouve aucun tuple matching le motif
		if (res == null)
			// on attend (en se bloquant sur une condition) une écriture d'un tuple qui matche le motif
			res = takeInFuture(template);
		return res;
	}

	// Méthode pour extraire un tuple correspondant au motif
	// si on trouve aucun tuple avec tryTake
	private Tuple takeInFuture(Tuple template) {

		//Booleen qui indique qu'on a efféctué l'extraction avec succés
		boolean took = false;
		//Le tuple à extraire
		Tuple res = null;

		while (!took) {
			// bloque jusqu'à ce qu'un bon tuple vient d'etre écrit
			res = sync.getTupleWhenExists(template, true);

			// Demander l'acces pour supprimer
			sync.beginModify();

			took = this.espace.remove(res);

			sync.endModify();
		}

		return res;
	}

	public Tuple read(Tuple template) {

		// Essayer de lire un tuple correspondant
		// et puis on révéille les opérations dans la file FIFO
		Tuple res = tryRead(template);

		if (res == null) {
			// si on trouve pas, on se bloque jusqu'à ce qu'un tuple correspondant apparait dans l'espace.
			res = sync.getTupleWhenExists(template, false);
		}
		
		return res;
	}

	public Tuple tryTake(Tuple template) {

		// Demander l'acces pour écrire(modifier) dans l'espace de tuple
		sync.beginModify();

		Tuple res = espace.rechercher(template, true);

		// Donner l'acces à l'opération suivante dans la file d'attente FIFO
		sync.endModify();

		return res;
	}
	public Tuple tryRead(Tuple template) {

		// Demander l'acces pour lire dans l'espace de tuple
		sync.beginRead();

		Tuple res = espace.rechercher(template, false);

		// Donner l'acces à l'opération suivante dans la file d'attente FIFO
		sync.endRead();

		return res;


	}

	public Collection<Tuple> takeAll(Tuple template) {

		// liste des resultats
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

		//Liste des resultats
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

		// Si on cherche immediatement, on fait une recherche non bloquante
		if (timing == eventTiming.IMMEDIATE) {

			if (mode == eventMode.READ) {
				res = this.tryRead(template);

			} else if (mode == eventMode.TAKE) {
				res = this.tryTake(template);

			} else return;

		}

		// Si on trouve pas ou si le timing est future,
		// on ajoute une alarme non bloquante (à reveiller par write)
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
