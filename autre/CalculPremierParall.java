package linda.autre;

import java.io.Serializable;

import linda.Linda;
import linda.Tuple;

public class CalculPremierParall {

	private int max;
	private int maxSeq;
	private Linda linda;
	
	public CalculPremierParall(Linda linda, int max, int maxSeq) {
		this.linda = linda;
		this.max = max;
		this.maxSeq = maxSeq;
	}

	public void calculer() {

		CalculPremiersSeq calculeur = new CalculPremiersSeq();

		int nbThreads = max / maxSeq;
		for (int i = 0 ; i < nbThreads ; i++) {
			final int index = i;
			new Thread() {
	            public void run() {
	            	
	            	Tuple tupleNombres = new Tuple();
	            	for (int j = index*maxSeq + 2  ; j<(index+1)*maxSeq + 2 ; j++) {
	            		tupleNombres.add(j);
	                }
                	linda.write(new Tuple(index, tupleNombres));
                	tupleNombres = calculeur.calculer(tupleNombres);
                	for (int k = 0 ; k < index ; k++) {
                		Tuple t = linda.read(new Tuple(k, Tuple.class));
                		tupleNombres = nonDivisible((Tuple)t.get(1), tupleNombres);
                	}
                	linda.write(new Tuple(index, tupleNombres));
            		System.out.println(tupleNombres.toString());
                	
	            }
			}.start();
		}
	}
	
	public Tuple nonDivisible(Tuple diviseurs, Tuple divisibles) {

		Tuple tuplePremiers = divisibles.deepclone();

        for (int i = 0 ; i < divisibles.size() ; i++) {
    		Serializable nb = divisibles.get(i);
        	for (int j = 0; j < diviseurs.size() ; j++) {
        		Serializable elt = diviseurs.get(j);
        		if ((Integer) nb % (Integer) elt == 0) {
        			tuplePremiers.remove(nb);
        		}
        	}
        }
        return tuplePremiers;
	}
	
	public static void main(String[] args) {

		final Linda linda = new linda.shm.CentralizedLinda();
        //final Linda linda = new linda.server.LindaClient("//localhost:4000/LindaServer");
		
		int max = 100000;
		int maxSeq = 1000;
		CalculPremierParall calculeurParall = new CalculPremierParall(linda, max, maxSeq);
		
		calculeurParall.calculer();
		
	}
	
	/*
		Application : Crible d'�ratosth�ne
		Clairement la version parall�le est plus rapide que celle s�quentielle
		( qui se bloque qu�on ai > 10000). La version parall�le quant � elle,
		elle est efficace et rapide quand maxSeq est plus petit mais pour des
		max tr�s tr�s grand on doit choisir un maxSeq adapt�.
		En fait, maxSeq repr�sente combien de nombres � traiter sur un seul thread,
		donc quand il diminue, on a plus de threads.
	 */
	
}

