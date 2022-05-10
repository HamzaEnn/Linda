package linda.cache;

import java.util.ArrayList;
import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Collection;
import java.util.HashMap;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.LindaServeur;
import linda.server.RemoteCallback;
import linda.server.RemoteCallbackImpl;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient_Cache implements Linda {

	private HashMap<Tuple, Integer> cache;
	LindaServeur linda;
	private static String topic = "Topic";
	private static ConnectionFactory connectionFactory;
	private static Connection connection;
	private static Session session;
	private static Destination destination;
	private static MessageConsumer consumer;
	private static MessageListener listener;
	/** Initializes the Linda implementation.
	 *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
	 */
	public LindaClient_Cache(String serverURI) {
		try {
			// get the stub of the server object from the rmiregistry
			this.linda = (LindaServeur) Naming.lookup(serverURI);
			this.cache = new HashMap<Tuple, Integer>();
			
			connectionFactory = new ActiveMQConnectionFactory(serverURI);
			
			connection = connectionFactory.createConnection();
			
			connection.start();
			
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			
			destination = session.createTopic(topic);
			
			consumer = session.createConsumer(destination);
			
			listener = new MessageListener() {
        		public void onMessage(Message msg) {
        			try {
        				ObjectMessage obj = (ObjectMessage) msg;
        				Tuple t = (Tuple) obj;
        				retirer(t);
        			} catch (Exception ex) {
        				ex.printStackTrace();
    				}
        		}
			};
			
			consumer.setMessageListener(listener);
	
		} catch (Exception exc) { 
			exc.printStackTrace();
		}
	}
	
	private Tuple rechercher_cache(Tuple template, boolean take) {
		Tuple res = null;
		for (Tuple t : cache.keySet()){
			if (t.matches(template)) {
				if (take) {
					if (cache.get(t) > 1) {
						cache.put(t, cache.get(t) - 1);
					} else {
						cache.remove(t);
					}
				}
				res = t;
				break;
			} else {
				res = null;
			}
		}
		return res;
	}
	
	private boolean retirer(Tuple target) {
		Boolean taken = false;
		for (Tuple t : cache.keySet()){
			if (t.equals(target)) {
				if (cache.get(t) > 1) {
					cache.put(t, cache.get(t) - 1);
				} else {
					cache.remove(t);
				}
				taken = true;
				break;
			} else {
				taken = false;
			}
		}
		return taken;
	}

	@Override
	public void write(Tuple t) {
		try {
			linda.write(t);
			for (Tuple tuple : cache.keySet()){
				if (tuple.equals(t)) {
					if (cache.get(t) > 1) {
						cache.put(t, cache.get(t) + 1);
					} else {
						cache.put(t, 1);
					}
					break;
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Tuple take(Tuple template) {
		try {
			return linda.take(template);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple read(Tuple template) {
		try {
			Tuple res = rechercher_cache(template, false);
			if (res != null) {
				return res;
			}
			res = linda.read(template);
			cache.put(res, 1);
			return res;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple tryTake(Tuple template) {
		try {
			return linda.tryTake(template);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple tryRead(Tuple template) {
		try {
			Tuple res = rechercher_cache(template, false);
			if (res != null) {
				return res;
			}
			return linda.tryRead(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		try {
			return linda.takeAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		try {
			return linda.readAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback){
		try {
			RemoteCallback cb = new RemoteCallbackImpl(callback);
			linda.eventRegister(mode, timing, template, cb);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void debug(String prefix) {
		try {
			linda.debug(prefix);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
