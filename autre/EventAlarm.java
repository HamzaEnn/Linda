package linda.autre;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Tuple;

public class EventAlarm {

	Callback callback;
	eventMode mode;
	Tuple template;

	public EventAlarm(Tuple t, Callback cb, eventMode em) {
		callback = cb;
		mode = em;
		template = t;
	}
	
	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public eventMode getMode() {
		return mode;
	}

	public void setMode(eventMode mode) {
		this.mode = mode;
	}
	
	public Tuple getTemplate() {
		return template;
	}

	public void setTemplate(Tuple template) {
		this.template = template;
	}

	public boolean contains(Tuple t) {
		return t.matches(template);
	}

}
