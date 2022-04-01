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

	private Lock lockFIFO;
	
	//modify = write or take
	private Lock lockModify;
	private Lock lockAlarm;
	private Lock lockEventReg;
	private Condition ap;
	private Condition sas;
	private int nbReaders = 0;
	private int nbDemModif = 0;
	private boolean writing = false;
	private List<TupleAlarm> alarms;
	private List<EventAlarm> events;
 
	public Synchronization () {
		lockFIFO = new ReentrantLock();
		lockModify = new ReentrantLock();
		lockAlarm = new ReentrantLock();
		lockEventReg = new ReentrantLock();
		ap = lockFIFO.newCondition();
		sas = lockModify.newCondition();
		alarms = new ArrayList<TupleAlarm>();
		events = new ArrayList<EventAlarm>();
		
	}
	
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
	
	public void endRead() {
		lockFIFO.lock();
		lockModify.lock();

		nbReaders --;
		if (nbReaders == 0) {
			if (nbDemModif != 0)
				sas.signal();
			else
				ap.signal();
		}

		lockModify.unlock();
		lockFIFO.unlock();

	}
	
	public void beginModify() {
		
		lockFIFO.lock();

		while (!((!writing) && (nbReaders == 0))) {
			try {
				ap.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lockModify.lock();

		while(nbReaders > 0) {
			try {
				sas.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		writing = true;

		lockModify.unlock();

		lockFIFO.unlock();
	}
	
	public void endModify() {
		lockFIFO.lock();
			writing = false;
			ap.signal();
		lockFIFO.unlock();
	}
	
	public List<Condition> getConditionsToAwake(Tuple tuple) {

		List<Condition> resultat = new ArrayList<Condition>();

		for (TupleAlarm alarm : this.alarms) {
			if (alarm.containsTuple(tuple)) {
				resultat.add(alarm.getCondition());
			}
			alarm.setResult(tuple);
		}

		return resultat;
	}
	
	public void wakeConditions(Tuple t) {

		lockAlarm.lock();
		
		List<Condition> conditions = getConditionsToAwake(t);
		for (Condition cnd : conditions) {
			cnd.signal();
			
		}

		lockAlarm.unlock();

	}

	public Tuple getTupleWhenExists(Tuple template) {
		lockAlarm.lock();
		Condition condition = lockAlarm.newCondition();
		TupleAlarm alarm = new TupleAlarm(template, condition);
		this.alarms.add(alarm);

		try {
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Tuple res = alarm.getResultat();
		this.alarms.remove(alarm);

		lockAlarm.unlock();

		return res;
	}
	
	public Tuple sleepTake(Tuple template) {
		lockAlarm.lock();
		Condition condition = lockAlarm.newCondition();
		TupleAlarm alarm = new TupleAlarm(template, condition);
		this.alarms.add(alarm);

		try {
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		lockAlarm.unlock();
		
		return alarm.getResultat();
	}
	
	public void addEventAlarm(Tuple template, Callback cb, eventMode em) {
		lockEventReg.lock();

		EventAlarm ea = new EventAlarm(template, cb, em);
		events.add(ea);

		lockEventReg.unlock();
	}
	
	public void wakeEventReg (Tuple t, EspaceTuples space) {
		lockEventReg.lock();
		int i = 0;

		for (EventAlarm event : this.events) {
			if (event.contains(t)) {
				if (event.getMode() == eventMode.TAKE) {
					beginModify();
					if (space.remove(t)) {
						events.remove(event);
						event.callback.call(t);
					}
					endModify();
					break;
				}else {
					if (space.getAll().contains(t)){
						event.getCallback().call(t);
					}
				}	
			}
		}

		lockEventReg.unlock();

	}
	
	public void debug(String prefix) {
		System.out.println(prefix);
	}
}
