package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.Callback;
import linda.Tuple;

public class RemoteCallbackImpl extends UnicastRemoteObject implements RemoteCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Callback cb;
	public RemoteCallbackImpl(Callback cb) throws RemoteException {
		this.cb = cb;
	}
	@Override
	public void call(Tuple t) throws RemoteException  {
		cb.call(t);
		
	}

}
