package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import linda.Tuple;

public interface RemoteCallback extends Remote {

	public void call(Tuple t) throws RemoteException;
}
