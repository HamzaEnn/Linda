package linda.autre;

import java.io.Serializable;
import linda.Tuple;

public class CalculPremiersSeq {

	// id utilisé dans les versions parallélisées
	public Tuple calculer(Tuple tupleNombres) {
		Tuple temp = tupleNombres.deepclone();
		Tuple tuplePremiers = new Tuple();
		
        while (!tupleNombres.isEmpty()) {
    		Serializable premier = tupleNombres.getFirst();
    		tuplePremiers.add(premier);
    		tupleNombres.remove(premier);
    		temp.remove(premier);
        	for (int i = 0; i < tupleNombres.size() ; i++) {
        		Serializable elt = tupleNombres.get(i);
        		if ((Integer) elt % (Integer) premier == 0) {
        			temp.remove(elt);
        		}
        	}
        	tupleNombres = temp.deepclone();
        }

        return tuplePremiers;
	}

	public static void main(String[] args) {
		int borneMax = 100000;
		
        CalculPremiersSeq calculeur = new CalculPremiersSeq();
        
        Tuple t = new Tuple();
        for (int i = 2 ; i<=borneMax ; i++) {
        	t.add(i);
        }
        Tuple premiers = calculeur.calculer(t);

        System.out.println(premiers.toString());
	}

}
