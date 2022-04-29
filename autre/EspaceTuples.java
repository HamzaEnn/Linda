package linda.autre;

import java.util.ArrayList;

import linda.Tuple;

public class EspaceTuples {

	// L'espace contenant les tuples
	private ArrayList<Tuple> espace;
	
	// Constructeur par defaut
	public EspaceTuples() {
		this.espace = new ArrayList<Tuple>();
	}
	
	// Retourner la taille de l'espace de tuples
	public int getSize() {
		return this.espace.size();
	}
	
	// Retourner un element de l'espace � partir d'un indice
	public Tuple get(int index) {
		return this.espace.get(index);
	}
	
	public ArrayList<Tuple> getAll() {
		return new ArrayList<Tuple>(this.espace);
	}
	
	// Supprimer un element de l'espace de tuples � partir d'un indice
	public void remove(int index) {
		this.espace.remove(index);
	}
	
	public boolean remove(Tuple tuple) {
		return this.espace.remove(tuple);
	}
	
	public void add(Tuple tuple) {
		this.espace.add(tuple);
	}
	
	// Chercher un element de l'espace � partir d'un tuple template.
	public Tuple rechercher(Tuple template, Boolean take) {
		Tuple res = null;
		int taille = this.getSize();
		int i = 0;
		Boolean continuer = true;
		while (continuer && i<taille) {
			res = this.get(i);
			if (res.matches(template)) {
				continuer = false;
				if (take) {
					this.remove(i);
				}
			} else {
				res = null;
			}
			i++;
		}
		return res;
	}
}
