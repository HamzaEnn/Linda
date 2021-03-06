package linda.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Linda;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.shm.CentralizedLinda;
import linda.Tuple;

public class LindaServeurImpl extends UnicastRemoteObject implements LindaServeur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Linda linda;

	public LindaServeurImpl() throws RemoteException {
		this.linda = new linda.shm.CentralizedLinda();
	}

	public LindaServeurImpl(CentralizedLinda l) throws RemoteException {
		this.linda = l;
	}
	
	public void changeLinda(CentralizedLinda l) throws RemoteException {
		this.linda = l;
	}

	@Override
	public void write(Tuple t) throws RemoteException {
		linda.write(t);

	}

	@Override
	public Tuple take(Tuple template) throws RemoteException {
		return linda.take(template);
	}

	@Override
	public Tuple read(Tuple template) throws RemoteException {
		return linda.read(template);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException {
		return linda.tryTake(template);
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException {
		return linda.tryRead(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		return linda.takeAll(template);
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

	public static void main(String args[], CentralizedLinda l) {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 4000;
		}
		System.out.println("nouveau serveur au port: "+port);
		String URL;
		/*try {
			Integer I = new Integer(args[0]); port = I.intValue();
		} catch (Exception ex) {
			System.out.println(" Please enter: java HelloImpl <port>"); return;
		}*/
		try {
			// Launching the naming service ??? rmiregistry ??? within the JVM
			LocateRegistry.createRegistry(port);
			// Create an instance of the server object
			LindaServeur linda;
			if (l == null) {
				linda = new LindaServeurImpl();
			} else {
				linda = new LindaServeurImpl(l);
			}
			// compute the URL of the server
			URL = "//localhost:"+port+"/LindaServer";
			Naming.rebind(URL, linda);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
