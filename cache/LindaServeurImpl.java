package linda.server;

import java.rmi.Naming;
import javax.jms.*;

import org.apache.activemq.*; 

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Linda;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;

public class LindaServeurImpl extends UnicastRemoteObject implements LindaServeur {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Linda linda;
	private static String topic = "Topic";
	private static ConnectionFactory connectionFactory;
	private static Connection connection;
	private static Session session;
	private static Destination destination;
	private static MessageProducer producer;
	

	protected LindaServeurImpl() throws RemoteException {
		this.linda = new linda.shm.CentralizedLinda();
	}

	@Override
	public void write(Tuple t) throws RemoteException {
		linda.write(t);

	}

	@Override
	public Tuple take(Tuple template) throws RemoteException {
		Tuple res = linda.take(template);
		ObjectMessage obj;
		try {
			obj = session.createObjectMessage(res);
			producer.send(obj);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public Tuple read(Tuple template) throws RemoteException {
		return linda.read(template);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException {
		Tuple res = linda.tryTake(template);
		if (res != null) {
			ObjectMessage obj;
			try {
				obj = session.createObjectMessage(res);
				producer.send(obj);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException {
		return linda.tryRead(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		Collection<Tuple> res = linda.takeAll(template);
		if (res.size() > 0){
			for (Tuple t : res) {
				ObjectMessage obj;
				try {
					obj = session.createObjectMessage(t);
					producer.send(obj);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		return linda.readAll(template);
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback)
			throws RemoteException {
		CallbackServeur cb = new CallbackServeur(callback);
		linda.eventRegister(mode, timing, template, cb);

	}

	@Override
	public void debug(String prefix) throws RemoteException {
		linda.debug(prefix);
	}

	public static void main(String args[]) {
		int port = 4002; String URL;
		/*try {
			Integer I = new Integer(args[0]); port = I.intValue();
		} catch (Exception ex) {
			System.out.println(" Please enter: java HelloImpl <port>"); return;
		}*/
		try {
			// Launching the naming service � rmiregistry � within the JVM
			//Registry registry = LocateRegistry.createRegistry(port);
			// Create an instance of the server object
			LindaServeur linda = new LindaServeurImpl();
			// compute the URL of the server
			URL = "//localhost:"+port+"/LindaServer";
			Naming.rebind(URL, linda);
			
			connectionFactory = new ActiveMQConnectionFactory(URL);
			
			connection = connectionFactory.createConnection();
			
			connection.start();
			
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			
			destination = session.createTopic(topic);
			
			producer = session.createProducer(destination);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
