package linda.server;

import linda.Callback;
import linda.Tuple;

public class CallbackServeur implements Callback {

	RemoteCallback rcb;
	
	public CallbackServeur(RemoteCallback cb) {
		rcb = cb;
	}

	public void call(Tuple t) {
		try {
			rcb.call(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
