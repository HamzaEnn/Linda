package linda.autre;
import java.util.concurrent.locks.Condition;

import linda.Tuple;

public class TupleAlarm {

	private Tuple template;
	private Condition condition;
	private Tuple result = null;
	private Boolean take;
	
	public TupleAlarm(Tuple _template, Condition _condition, Boolean _take) {
		template = _template;
		condition = _condition;
		take = _take;
	}

	public boolean taken() {
		return take;
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
