package gui;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.Adresse;
import core.Instruction;
import core.Joueur;
import core.Memoire;
import core.Operation;

public class Simulateur implements Runnable {
	private Joueur j1;
	private Joueur j2;
	private int starter;
	private long delay;
	public Memoire mem;
	private int cpt;
	private boolean running;
	public AfficheurMod affm;

	public Simulateur(AfficheurMod affm) {
		mem = new Memoire("");
		starter = 1;
		j1 = null;
		j2 = null;
		cpt = 0;
		this.affm = affm;
	}

	public Simulateur(AfficheurMod affm, Situation situ) {
		mem = new Memoire("");
		mem = situ.mem;
		starter = situ.tour;

		j1 = situ.j1;
		j2 = situ.j2;
		cpt = 0;
		this.affm = affm;
	}

	public void creerJoueur(int joueur, LinkedList<Operation> lo) {
		synchronized (affm.lock) {
			Adresse p = new Adresse();
			int cpt = 0;
			if (joueur == 1) {
				j1 = new Joueur(lo);
				j1.processus.add(p);
				for (Operation op1 : j1.operations) {
					if (cpt < 100) {
						mem.O[p.get()] = op1;
						p = p.plus(new Adresse(1));
						cpt++;
					} else {
						break;
					}
				}
				affm.actualiseur.start();
			} else if (joueur == 2) {
				j2 = new Joueur(lo);

				int m = (int) (Math
						.floor(((800 - j1.operations.size() - j2.operations
								.size()) * Math.random())) + 1);
				p = p.plus(new Adresse(m));
				j2.processus.add(p);
				for (Operation op : j2.operations) {
					if (cpt < 100) {
						mem.O[p.get()] = op;
						p = p.plus(new Adresse(1));
						cpt++;
					} else {
						break;
					}
				}
			}
			if (cpt == 100) {
				JOptionPane
						.showMessageDialog(null, "Ce joueur est trop long !");
			}
		}
	}

	public void enAvant() {
		synchronized (affm.lock) {
			affm.archive.archiver(new Situation(mem, j1, j2, starter, affm));
			if (starter == 1) {
				j1.jouer(mem);

				if (j2 != null) {
					starter = 2;
				}
				cpt++;
			} else if (starter == 2) {
				j2.jouer(mem);
				starter = 1;
			}
			if (cpt > 0) {
				for (int i = 0; i < 800; i++) {
					if (affm.archive.nextElement().couleur[i].equals(Color.green) && getProcess(1) != null) {
						if (!getProcess(1).contains(new Adresse(i))) {
							affm.tab[i].setBackground(Color.white);
						}
					}
					if (affm.archive.nextElement().couleur[i].equals(Color.blue) && getProcess(2) != null) {
						if (!getProcess(2).contains(new Adresse(i))) {
							affm.tab[i].setBackground(Color.white);
						}
					}
					if (!mem.O[i].equals(affm.archive.nextElement().mem.O[i])) {
						if (starter == 1)
							affm.tab[i].setBackground(Color.cyan);
						else
							affm.tab[i].setBackground(Color.yellow);
					}
				}
			}
		}

	}

	public long getDelay() {
		return delay;
	}

	public int getNombreJoueurs() {
		if (j1 == null) {
			return 0;
		} else if (j2 == null) {
			return 1;
		} else {
			return 2;
		}
	}

	public String getOp(Adresse a) {
		return mem.O[a.get()].toString();
	}

	public LinkedList<Adresse> getProcess(int joueur) {
		if (joueur == 1 && j1 != null && !j1.processus.isEmpty()) {
			return new LinkedList<Adresse>(j1.processus);
		} else if (joueur == 2 && j2 != null && !j2.processus.isEmpty()) {
			return new LinkedList<Adresse>(j2.processus);
		} else {
			return null;
		}
	}

	public void pause() {
		running = false;
		Thread.currentThread().interrupt();
	}

	public void putMov(Adresse a) {
		mem.O[a.get()].ins = Instruction.MOV;
	}

	public void reinitialiser(Situation situ, boolean total) {
		mem = new Memoire(situ.mem);
		j1 = situ.j1;
		j2 = situ.j2;
		starter = situ.tour;
		if (total)
			cpt = 0;
		else
			cpt = cpt - 1;
		for (int i = 0; i < 800; i++) {
			affm.tab[i].setBackground(situ.couleur[i]);
		}
	}

	public void reinitialiserMemoire() {
		mem = new Memoire("");
	}

	public void restart() {
		start();
	}

	@Override
	public void run() {

		if (getNombreJoueurs() == 1) {
			while (!j1.processus.isEmpty() && cpt < 1000000 && running) {
				enAvant();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		else if (getNombreJoueurs() == 2) {
			while (!j1.processus.isEmpty() && !j2.processus.isEmpty()
					&& cpt < 1000000 && running) {
				enAvant();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		if (j1 != null && j1.processus != null && j1.processus.isEmpty()
				&& running) {
//			JOptionPane.showMessageDialog(null, "Joueur 2 a gagné.");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FinalMsg Err1 = new FinalMsg("Joueur 2 a gagné.");
					Err1.setVisible(true);
				}
			});
			pause();
			affm.getMenu().getPause().setEnabled(false);
		}

		else if (j2 != null && j2.processus != null && j2 != null
				&& j2.processus.isEmpty() && running) {
			//			JOptionPane.showMessageDialog(null, "Joueur 1 a gagné.");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FinalMsg Err1 = new FinalMsg("Joueur 1 a gagné.");
					Err1.setVisible(true);
				}
			});
			pause();
			affm.getMenu().getPause().setEnabled(false);
		}

		else if (running) {
//			JOptionPane.showMessageDialog(null, "Egalité.");
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FinalMsg Err1 = new FinalMsg("Egalité.");
					Err1.setVisible(true);
				}
			});
			pause();
			affm.getMenu().getPause().setEnabled(false);
		}
	}

	public void setSpeed(int d) {
		boolean wasRunning = false;
		if (running) {
			wasRunning = true;
		}
		pause();
		affm.pause(delay);
		if (d < 4) {
			delay = (long) Math.pow(10, 4 - d);
		} else {
			delay = 0;
		}
		if (wasRunning) {
			restart();
		}
	}

	public void start() {
		running = true;
		Thread a = new Thread(null, this);
		a.start();
	}

	public boolean isRunning() {
		return running;
	}
}
