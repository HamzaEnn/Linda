package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	private List<Lock> queue = new ArrayList<Lock>();
	private List<Tuple> tuplespace = Collections.synchronizedList(new ArrayList<Tuple>());
	private Lock notFoundTuple = new ReentrantLock();

	public CentralizedLinda() {
	}
	
	/**
	 * Auxiliary function putting take and read waiting for an update of the tuplespace.
	 */
	public void waiting() {
		try {
			synchronized(notFoundTuple) {
				queue.add(notFoundTuple);
				notFoundTuple.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Auxiliary function translating the behavior of take, read, tryTake and tryRead.
	 */
	public Tuple browsing(Tuple template, Boolean remove, Boolean blockingImpl, int index) {
		// Looking for template in the tuplespace.
		synchronized(tuplespace) {
			Iterator<Tuple> it = tuplespace.listIterator(index);
			while (it.hasNext()) {
				Tuple tuple = it.next();
				if (tuple.matches(template)) {
					if (remove) it.remove();
					return tuple;
				}	
			}	
		}

		// If template is not in the tuplespace:
		if (blockingImpl) {
			waiting();
			return browsing(template, remove, blockingImpl, index);
		} else {
			return null;
		}
	}
	
	/**
	 * Auxiliary function translating the behavior of takeAll and readAll.
	 */
	public Collection<Tuple> browsingAll(Tuple template, Boolean remove) {
		Collection<Tuple> collection = new ArrayList<Tuple>();
		synchronized(tuplespace) {
			Iterator<Tuple> it = tuplespace.listIterator();	
			while (it.hasNext()) {
				Tuple tuple = it.next();
	    		if (tuple.matches(template)) {
	    			collection.add(tuple);
	    			if (remove) it.remove();
	    		}
			}
		}
		return collection;
	}
    
	@Override
    /**
     * Adds a tuple t to the tuplespace.
     */
    public void write(Tuple t) {
		// Adding a tuple in the tuplespace.
		synchronized(tuplespace) {
			tuplespace.add(t);
		}
    	
    	// Notifying take and read that a new tuple is in the tuplespace.
    	synchronized(notFoundTuple) {
    		if (queue.size() != 0) {
    			Lock waitingTuple = queue.get(0);
        		queue.remove(waitingTuple);
        		waitingTuple.notify();
    		}
    	}
	}
	
	@Override
    /** 
     * Returns a tuple matching the template and removes it from the tuplespace.
     * Blocks if no corresponding tuple is found.
     */
    public Tuple take(Tuple template) {
		return browsing(template, true, true, 0);
    }

	@Override
    /** 
     * Returns a tuple matching the template and leaves it in the tuplespace.
     * Blocks if no corresponding tuple is found.
     */
    public Tuple read(Tuple template) {
		return browsing(template, false, true, 0);
	}

	@Override
    /** 
     * Returns a tuple matching the template and removes it from the tuplespace.
     * Returns null if none found.
     */
    public Tuple tryTake(Tuple template) {
		return browsing(template, true, false, 0);
    }

	@Override
    /** 
     * Returns a tuple matching the template and leaves it in the tuplespace.
     * Returns null if none found.
     */
    public Tuple tryRead(Tuple template) {
		return browsing(template, false, false, 0);
	}

	@Override
    /** 
     * Returns all the tuples matching the template and removes them from the tuplespace.
     * Returns an empty collection if none found (never blocks).
     * Note: there is no atomicity or consistency constraints between takeAll and other methods;
     * for instance two concurrent takeAll with similar templates may split the tuples between the two results.
     */
    public Collection<Tuple> takeAll(Tuple template) {
		return browsingAll(template, true);
	}

	@Override
    /** 
     * Returns all the tuples matching the template and leaves them in the tuplespace.
     * Returns an empty collection if none found (never blocks).
     * Note: there is no atomicity or consistency constraints between readAll and other methods;
     * for instance (write([1]);write([2])) || readAll([?Integer]) may return only [2].
     */
    public Collection<Tuple> readAll(Tuple template) {
		return browsingAll(template, false);
	}

    public enum eventMode { READ, TAKE };
    public enum eventTiming { IMMEDIATE, FUTURE };

    @Override
    /**
     * To debug, prints any information it wants (e.g. the tuples in tuplespace or the registered callbacks),
     * prefixed by <code>prefix</code>.
     */
    public void debug(String prefix) {
    	System.out.println(prefix + " TERMINATED");
	}

    /** Registers a callback which will be called when a tuple matching the template appears.
     * If the mode is Take, the found tuple is removed from the tuplespace.
     * The callback is fired once. It may re-register itself if necessary.
     * If timing is immediate, the callback may immediately fire if a matching tuple is already present; if timing is future, current tuples are ignored.
     * Beware: a callback should never block as the calling context may be the one of the writer (see also {@link AsynchronousCallback} class).
     * Callbacks are not ordered: if more than one may be fired, the chosen one is arbitrary.
     * Beware of loop with a READ/IMMEDIATE re-registering callback !
     *
     * @param mode read or take mode.
     * @param timing (potentially) immediate or only future firing.
     * @param template the filtering template.
     * @param callback the callback to call if a matching tuple appears.
     */
	@Override
	public void eventRegister(linda.Linda.eventMode mode, linda.Linda.eventTiming timing, Tuple template,
			Callback callback) {
		new Thread() {
			public void run() {
				Tuple t;
				if (mode == linda.Linda.eventMode.READ) {
					if (timing == Linda.eventTiming.IMMEDIATE){
						t = read(template);
					} else {
						int index = tuplespace.size() - 1;
						t = browsing(template, false, true, index);
					}
				} else {
					if (timing == Linda.eventTiming.IMMEDIATE){
						t = take(template);
					} else {
						int index = tuplespace.size() - 1;
						t = browsing(template, true, true, index);
					}
				}
				callback.call(t);
			}
		}.start();
	}
}
