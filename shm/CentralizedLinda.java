package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private ArrayList<Tuple> espace;
	private HashMap<Integer, ArrayList<Tuple>> espacesEventReg;
	private ReentrantLock moniteur;
	private Condition AccesRead;
	private Condition AccesTake;
	private Condition AccesReadAll;
	private Condition AccesTakeAll;
	private Condition AccesTryRead;
	private Condition AccesTryTake;
	private Condition AccesWrite;
	private Condition AccesEventRegister;
	private int nbDemandeWrite = 0;
	private int nbDemandeTake = 0;
	private int nbDemandeRead = 0;
	private int nbDemandeTakeAll = 0;
	private int nbDemandeReadAll = 0;
	private int nbDemandeTryRead = 0;
	private int nbDemandeTryTake = 0;
	private int nbDemandeEventRegister = 0;
	private int nbRead = 0;
	static enum Etat{writing, reading, taking};
	private Etat etat = null;
	private int nbER = 0; //key to tuples in hashmap for EventRegister

	public CentralizedLinda() {
		espace = new ArrayList<Tuple>();
		espacesEventReg = new HashMap<Integer, ArrayList<Tuple>>();
		moniteur = new ReentrantLock();
		AccesRead = moniteur.newCondition();
		AccesTake = moniteur.newCondition();
		AccesReadAll = moniteur.newCondition();
		AccesTakeAll = moniteur.newCondition();
		AccesTryRead = moniteur.newCondition();
		AccesTryTake = moniteur.newCondition();
		AccesWrite = moniteur.newCondition();
		AccesEventRegister = moniteur.newCondition();
	}

	private Tuple rechercher(Tuple template, ArrayList<Tuple> univers, Boolean take) {
		Tuple res = null;
		int taille = univers.size();
		int i = 0;
		Boolean continuer = true;
		while (continuer && i<taille) {
			res = univers.get(i);
			if (res.matches(template)) {
				continuer = false;
				if (take) {
					univers.remove(i);
				}
			} else {
				res = null;
			}
			i++;
		}
		return res;
	}

	public Tuple tryTake(Tuple template) {
		moniteur.lock();
		nbDemandeTryTake ++;
		while (!((etat != Etat.writing) && (nbRead == 0) && (etat != Etat.taking))) {
			try {
				AccesTryTake.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nbDemandeTryTake --;
		etat = Etat.taking;
		moniteur.unlock();

		Tuple res = rechercher(template, this.espace, true);
		this.espacesEventReg.forEach((key, value) -> value.remove(res));

		moniteur.lock();
		etat = null;
		if (nbDemandeTryRead > 0) {
			AccesTryRead.signal();
		} else if (nbDemandeWrite >0) {
			AccesWrite.signal();
		} else if (nbDemandeTake >0) {
			AccesTake.signal();
		} else if (nbDemandeRead >0) {
			AccesRead.signal();
		} else if (nbDemandeReadAll >0) {
			AccesReadAll.signal();
		} else if (nbDemandeTakeAll >0) {
			AccesTakeAll.signal();
		} else if (nbDemandeEventRegister > 0) {
			AccesEventRegister.signal();
		} else {
			AccesTryTake.signal();
		}
		moniteur.unlock();

		return res;
	}

	public Tuple tryRead(Tuple template) {
		moniteur.lock();
		nbDemandeTryRead ++;
		while (!((etat != Etat.writing) && (etat != Etat.taking))) {
			try {
				AccesTryRead.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		etat = Etat.reading;
		nbDemandeTryRead --;
		nbRead ++;
		moniteur.unlock();

		Tuple res = rechercher(template, this.espace, false);

		moniteur.lock();
		etat = null ;
		nbRead --;
		if (nbRead == 0) {
			if (nbDemandeWrite > 0) {
				AccesWrite.signal();
			} else if (nbDemandeTake > 0) {
				AccesTake.signal();
			} else if (nbDemandeRead > 0) {
				AccesRead.signal();
			} else if (nbDemandeReadAll >0) {
				AccesReadAll.signal();
			} else if (nbDemandeTakeAll >0) {
				AccesTakeAll.signal();
			} else if (nbDemandeEventRegister > 0) {
				AccesEventRegister.signal();
			} else if (nbDemandeTryTake > 0) {
				AccesTryTake.signal();
			} else {
				AccesTryRead.signal();
			}
		}
		moniteur.unlock();

		return res;

	}

	public void write(Tuple t) {
		moniteur.lock();
		nbDemandeWrite ++;
		while (!((etat != Etat.writing) && (nbRead == 0) && (etat != Etat.taking))) {

			try {
				AccesWrite.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		nbDemandeWrite --;
		etat = Etat.writing;
		moniteur.unlock();

		espace.add(t.deepclone());

		espacesEventReg.forEach((i,e) -> e.add(t));

		moniteur.lock();
		etat = null;
		if (nbDemandeTake > 0) {
			AccesTake.signal();
		} else if (nbDemandeRead > 0) {
			AccesRead.signal();
		} else if (nbDemandeReadAll >0) {
			AccesReadAll.signal();
		} else if (nbDemandeTakeAll >0) {
			AccesTakeAll.signal();
		} else if (nbDemandeEventRegister > 0) {
			AccesEventRegister.signal();
		} else if (nbDemandeTryTake > 0) {
			AccesTryTake.signal();
		}else if (nbDemandeTryRead > 0) {
			AccesTryRead.signal();
		} else {
			AccesWrite.signal();
		}
		moniteur.unlock();
	}

	public Tuple take(Tuple template) {
		boolean found = false;
		Tuple res = null;
		while (!found) {
			moniteur.lock();
			nbDemandeTake ++;
			while (!((etat != Etat.writing) && (nbRead == 0) && (etat != Etat.taking))) {
				try {
					AccesTake.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			nbDemandeTake --;
			etat = Etat.taking;
			moniteur.unlock();
			res = rechercher(template, this.espace, true);
			if (res != null) {
				found = true;
				final Tuple resTemp = res;
				this.espacesEventReg.forEach((key,value) -> value.remove(resTemp));
			}

			moniteur.lock();
			etat = null;
			if (nbDemandeRead > 0) {
				AccesRead.signal();
			}else if (nbDemandeReadAll > 0){
				AccesReadAll.signal();
			}else if (nbDemandeTakeAll > 0){
				AccesTakeAll.signal();
			}else if (nbDemandeEventRegister > 0){
				AccesEventRegister.signal();
			}else if (nbDemandeTryTake > 0) {
				AccesTryTake.signal();
			}else if (nbDemandeTryRead > 0) {
				AccesTryRead.signal();
			}else if (nbDemandeWrite > 0) {
				AccesWrite.signal();
			}else {
			}
			moniteur.unlock();
		}

		return res;
	}

	public Tuple read(Tuple template) {

		boolean found = false;
		Tuple res = null;
		while (!found) {
			moniteur.lock();
			nbDemandeRead ++;
			while (!((etat != Etat.writing) && (etat != Etat.taking))) {
				try {
					AccesRead.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			nbDemandeRead --;
			etat = Etat.reading;
			nbRead++;
			moniteur.unlock();

			res = rechercher(template, this.espace, false);
			if (res != null) {
				found = true;
			}

			moniteur.lock();
			etat = null;
			nbRead--;
			if (nbRead == 0) {
				if (nbDemandeReadAll > 0) {
					AccesReadAll.signal();
				}else if (nbDemandeTakeAll > 0) {
					AccesTakeAll.signal();
				} else if (nbDemandeEventRegister > 0) {
					AccesEventRegister.signal();
				}else if (nbDemandeTryTake > 0) {
					AccesTryTake.signal();
				}else if (nbDemandeTryRead > 0) {
					AccesTryRead.signal();
				}else if (nbDemandeWrite > 0) {
					AccesWrite.signal();
				}else if (nbDemandeTake > 0) {
					AccesTake.signal();
				} else {
					AccesRead.signal();
				}
			}
			moniteur.unlock();

		}
		return res;
	}

	public Collection<Tuple> takeAll(Tuple template) {

		ArrayList<Tuple> res = new ArrayList<Tuple>();
		moniteur.lock();
		nbDemandeTakeAll ++;
		while (!((etat != Etat.writing) && (nbRead == 0) && (etat != Etat.taking))) {
			try {
				AccesTakeAll.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nbDemandeTakeAll --;
		etat = Etat.taking;
		moniteur.unlock();

		ArrayList<Tuple> temp = new ArrayList<Tuple>(this.espace);
		for (Tuple t : temp) {
			if (t.matches(template)) {
				espace.remove(t);
				this.espacesEventReg.forEach((key, value) -> value.remove(t));
				res.add(t);
			}
		}

		moniteur.lock();
		etat = null;
		if (nbDemandeReadAll > 0) {
			AccesReadAll.signal();
		} else if (nbDemandeEventRegister > 0) {
			AccesEventRegister.signal();
		}else if (nbDemandeTryTake > 0) {
			AccesTryTake.signal();
		}else if (nbDemandeTryRead > 0) {
			AccesTryRead.signal();
		}else if (nbDemandeWrite > 0) {
			AccesWrite.signal();
		}else if (nbDemandeTake > 0) {
			AccesTake.signal();
		} else {
			AccesTakeAll.signal();
		}
		moniteur.unlock();
		return res;

	}

	public Collection<Tuple> readAll(Tuple template) {

		ArrayList<Tuple> res = new ArrayList<Tuple>();
		moniteur.lock();
		nbDemandeReadAll ++;
		while (!((etat != Etat.writing) && (etat != Etat.taking))) {
			try {
				AccesReadAll.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nbDemandeReadAll--;
		etat = Etat.reading;
		nbRead ++;
		moniteur.unlock();

		for (Tuple t : this.espace) {
			if (t.matches(template)) {
				res.add(t.deepclone());
			}
		}

		moniteur.lock();
		nbRead --;
		etat = null;
		if (nbRead == 0) {
			if (nbDemandeTakeAll > 0) {
				AccesTakeAll.signal();
			} else if (nbDemandeEventRegister > 0) {
				AccesEventRegister.signal();
			}else if (nbDemandeTryTake > 0) {
				AccesTryTake.signal();
			}else if (nbDemandeTryRead > 0) {
				AccesTryRead.signal();
			}else if (nbDemandeWrite > 0) {
				AccesWrite.signal();
			}else if (nbDemandeTake > 0) {
				AccesTake.signal();
			}else if (nbDemandeRead > 0) {
				AccesRead.signal();
			}else if (nbDemandeTakeAll > 0) {
				AccesTakeAll.signal();
			} else {
				AccesReadAll.signal();
			}
		}
		moniteur.unlock();

		return res;
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		new Thread() {
			public void run() {

				int nb = nbER;
				if (timing == eventTiming.FUTURE) {
					nbER ++;
					espacesEventReg.put(nb, new ArrayList<Tuple>());
				}

				boolean found = false;
				Tuple res = null;
				while (!found) {
					if (mode == eventMode.READ) {
						moniteur.lock();
						nbDemandeEventRegister ++;
						while (!((etat != Etat.writing) && (etat != Etat.taking))) {
							try {
								AccesEventRegister.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						nbDemandeRead --;
						etat = Etat.reading;
						nbRead++;
						moniteur.unlock();
					} else {
						moniteur.lock();
						nbDemandeEventRegister ++;
						while (!((etat != Etat.writing) && (nbRead == 0) && (etat != Etat.taking))) {
							try {
								AccesEventRegister.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						nbDemandeTake --;
						etat = Etat.taking;
						moniteur.unlock();
					}

					if (timing == eventTiming.IMMEDIATE) {
						res = rechercher(template, espace, (mode == eventMode.TAKE));
					} else {
						res = rechercher(template, espacesEventReg.get(nb), (mode == eventMode.TAKE));
					}
					if (res != null) {
						if (timing == eventTiming.FUTURE) {
							espace.remove(res);
						}
						found = true;
						callback.call(res);
						espacesEventReg.remove(nb);
						if (mode == eventMode.TAKE) {
							final Tuple resTemp = res;
							espacesEventReg.forEach((key, value) -> value.remove(resTemp));
						}
					}

					moniteur.lock();
					etat = null;
					if (mode == eventMode.READ) {
						nbRead--;
					}
					if (nbRead == 0) {
						if (nbDemandeTryTake > 0) {
							AccesTryTake.signal();
						}else if (nbDemandeTryRead > 0) {
							AccesTryRead.signal();
						}else if (nbDemandeWrite > 0) {
							AccesWrite.signal();
						}else if (nbDemandeTake > 0) {
							AccesTake.signal();
						} else if (nbDemandeRead > 0){
							AccesRead.signal();
						}else if (nbDemandeReadAll > 0) {
							AccesReadAll.signal();
						}else if (nbDemandeTakeAll > 0) {
							AccesTakeAll.signal();
						} else {
							AccesEventRegister.signal();
						}
					}
					moniteur.unlock();
				}
			}
		}.start();
	}

	public void debug(String prefix) {

	}

}
