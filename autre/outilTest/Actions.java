import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import linda.shm.CentralizedLinda;

public class Actions {

    private static List<Object> in;
    private static List<Object> out;
    private static List<Method> methods;
    private static Boolean init = false;

    public void Initialize(){
        this.in = new ArrayList<Object>();
        this.out = new ArrayList<Object>();
        this.methods = new ArrayList<Method>();


    }

	public static List<Object> getParameters(String word) {
		return null;
	}

    


}