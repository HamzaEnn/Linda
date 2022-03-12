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
		Application : Crible d'ératosthène
		Clairement la version parallèle est plus rapide que celle séquentielle
		( qui se bloque qu’on ai > 10000). La version parallèle quant à elle,
		elle est efficace et rapide quand maxSeq est plus petit mais pour des
		max très très grand on doit choisir un maxSeq adapté.
		En fait, maxSeq représente combien de nombres à traiter sur un seul thread,
		donc quand il diminue, on a plus de threads.
	 */
	
}

