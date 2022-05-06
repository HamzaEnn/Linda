package linda.autre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Tuple;
public class Synchronization {

	// Verou pour la file fifo
	private Lock lockFIFO;
	//	Verou pour les alarmes de take et read bloqués
	private Lock lockAlarm;
	//	Verou pour les alarmes de eventRegister
	private Lock lockEventReg;
	//	Condition d'acces fifo
	private Condition ap;
	//	Condition d'acces supplementaire pour modify (=write ou take)
	private Condition sas;
	//	Nombre de lecteurs
	private int nbReaders = 0;
	//	Nombre de demandes de take et write
	private int nbDemModif = 0;
	//	Booleen redaction
	private boolean writing = false;
	//	Liste des alarmes bloquantes pour les read et take
	private List<TupleAlarm> alarms;
	//	Liste des alarmes non bloquantes pour les eventRegister
	private List<EventAlarm> events;
 

	public Synchronization () {
		lockFIFO = new ReentrantLock();
		lockAlarm = new ReentrantLock();
		lockEventReg = new ReentrantLock();
		ap = lockFIFO.newCondition();
		sas = lockFIFO.newCondition();
		alarms = new ArrayList<TupleAlarm>();
		events = new ArrayList<EventAlarm>();
	}

	/**
	 * Demande acces à read
	 */
	public void beginRead() {

		lockFIFO.lock();

		while (writing) {
			try {
				ap.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		nbReaders ++;
		ap.signal();

		lockFIFO.unlock();

	}

	/**
	 * Terminer la lecture
	 */
	public void endRead() {

		lockFIFO.lock();

		nbReaders --;
		if (nbReaders == 0) {
			if (nbDemModif != 0)
				sas.signal();
			else
				ap.signal();
		}

		lockFIFO.unlock();

	}

	/**
	 * Demander l'acces à write ou take (modify)
	 */
	public void beginModify() {
		
		lockFIFO.lock();

		while (!((!writing) && (nbReaders == 0))) {
			try {
				ap.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//	Résoudre le problème de révéiller un modify quand on est en lecture
		while(nbReaders > 0) {
			try {
				sas.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		writing = true;

		lockFIFO.unlock();
	}

	/**
	 * Terminer modify
	 */
	public void endModify() {
		lockFIFO.lock();
			writing = false;
			ap.signal();
		lockFIFO.unlock();
	}

	/**
	 * Obtenir les conditions des alarmes des read et take à réveiller par le write
	 * @param tuple : le tuple cherché à faire passer à read et take
	 * @return
	 */
	public List<Condition> getConditionsToAwake(Tuple tuple) {

		//	Liste des conditions
		List<Condition> resultat = new ArrayList<Condition>();

		for (TupleAlarm alarm : this.alarms) {
			if (alarm.containsTuple(tuple)) {
				resultat.add(alarm.getCondition());
				alarm.setResult(tuple);
				alarms.remove(alarm);
				if (alarm.taken()) {
					break;
				}
			}
		}

		return resultat;
	}

	/**
	 * Réveiller les conditions de read/take correspondantes au tuple
	 * @param t : tuple écrit
	 */
	private void wakeConditions(Tuple t) {

		lockAlarm.lock();
		
		List<Condition> conditions = getConditionsToAwake(t);
		for (Condition cnd : conditions) {
			cnd.signal();
			
		}

		lockAlarm.unlock();

	}

	/**
	 * Obtenir le tuple correspondant au motif de read/take quand il figure dans l'espace (bloquant)
	 * @param template : motif
	 * @return : le tuple correspondant trouvé dans l'espace
	 */
	public Tuple getTupleWhenExists(Tuple template, Boolean take) {
		// Obtenir l'acces à "alarms" et à créer des conditions
		lockAlarm.lock();
		//Création d'une nouvelle alarme à réveiller quand le tuple existera
		Condition condition = lockAlarm.newCondition();
		TupleAlarm alarm = new TupleAlarm(template, condition, take);
		this.alarms.add(alarm);

		// Se bloquer jusqu'à l'apparition du tuple
		try {
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Obtenir le tuple à retourner
		Tuple res = alarm.getResultat();
		// Supprimer l'alarme
		this.alarms.remove(alarm);

		lockAlarm.unlock();

		return res;
	}

	/**
	 * Ajout d'une alarme non bloquante d'un eventRegister
	 * @param template : motif
	 * @param cb : callback
	 * @param em : eventmode (take ou read)
	 */
	public void addEventAlarm(Tuple template, Callback cb, eventMode em) {
		lockEventReg.lock();

		EventAlarm ea = new EventAlarm(template, cb, em);
		events.add(ea);

		lockEventReg.unlock();
	}
	
	private boolean wakeEventReg (Tuple t, EspaceTuples space) {
		lockEventReg.lock();

		// booleen specifiant si le tuple existe encore dans l'espace ou pas
		Boolean took = false;

		for (EventAlarm event : this.events) {
			if (event.contains(t)) {

				if (event.getMode() == eventMode.TAKE) {
					beginModify();
					if (space.remove(t)) {
						events.remove(event);
						event.callback.call(t);
					}
					endModify();
					took = true;
					break;

				}else {
					if (space.getAll().contains(t)){
						event.getCallback().call(t);
					}
				}

			}
		}

		lockEventReg.unlock();
		return took;

	}

	public void wakeUp(Tuple t, EspaceTuples space) {
		if (!wakeEventReg(t, space)) {
			wakeConditions(t);
		}
	}
	
	public void debug(String prefix) {
		System.out.println(prefix);
	}
}
