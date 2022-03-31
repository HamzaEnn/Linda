package linda.autre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;

import linda.Tuple;

public class TupleAlarm {

	private Tuple template;
	private Condition condition;
	private Tuple result = null;
	
	public TupleAlarm(Tuple _template, Condition _condition) {
		template = _template;
		condition = _condition;
	}

	public boolean containsTuple(Tuple t) {
		return (t.matches(template));
	}
	
	public Tuple getTemplate() {
		return this.template;
	}
	
	public Condition getCondition() {
		return this.condition;
	}

	public Tuple getResultat() {
		return result;
	}
	
	public void setResult(Tuple t) {
		this.result = t;
	}


}
