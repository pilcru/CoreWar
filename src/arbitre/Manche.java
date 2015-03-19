package arbitre;

import java.util.LinkedList;

import core.Adresse;
import core.Joueur;
import core.Memoire;
import core.Operation;

/**
Une manche représente l'affrontement élémentaire entre deux joueurs.
*/
public class Manche extends Thread {

	Joueur j1;
	Joueur j2;
	private static String commun = "manche";
	private String nom;
	int[] score;		// tableau des scores ([égalité, J1, J2])
	int starter;		// définit le premier joueur à jouer.
	long delay;			// définit la vitesse de jeu.

	public Manche(LinkedList<Operation> l1, LinkedList<Operation> l2, String s,
			int[] score, int starter) {
		j1 = new Joueur(l1);
		j2 = new Joueur(l2);
		nom = s;
		this.score = score;
		this.starter = starter;
		this.delay = 0;
	}

	public Manche(LinkedList<Operation> l1, LinkedList<Operation> l2, String s,
			int[] score, int starter, long delay) {
		j1 = new Joueur(l1);
		j2 = new Joueur(l2);
		nom = s;
		this.score = score;
		this.starter = starter;
		this.delay = delay;
	}

	@Override
	public void run() {
		commun = commun + nom;
		Memoire mem = new Memoire(nom);
		Adresse p = new Adresse();
		int taille = 0;

		j1.processus.add(p); 			//le joueur 1 commence à l'adresse 0.
		
		// recopiage du code du joueur 1 dans la mémoire
		for (Operation op1 : j1.operations) {
			if (taille < 100) {
				mem.O[p.get()] = op1;
				p = p.plus(new Adresse(1));
				taille++;
			} else {
				System.err.println("Ce joueur est trop long !");
				break;
			}
		}

		// détermination aléatoire de la position du joueur 2 tel que les joueurs
		// et leurs codes ne se chevauchent pas.
		int m = (int) (Math.floor(((800 - j1.operations.size() - j2.operations
				.size()) * Math.random())) + 1);
		p = p.plus(new Adresse(m));
		j2.processus.add(p);
		for (Operation op : j2.operations) {
			if (taille < 100) {
				mem.O[p.get()] = op;
				p = p.plus(new Adresse(1));
				taille++;
			} else {
				System.err.println("Ce joueur est trop long !");
				break;
			}
		}

		int cpt = 0;							// compteur de tours de jeu
		if (starter % 2 == 0) {
			while (!j1.processus.isEmpty() && !j2.processus.isEmpty()
					&& cpt < 1000000) {
				cpt++;
				j1.jouer(mem);					// Joueur 1 joue
				if (!j1.processus.isEmpty()) {	//si joueur 1 est toujours vivant...
					j2.jouer(mem);				// ... joueur 2 joue a son tour
				}
			}
		} else { //idem dans l'autre sens
			while (!j2.processus.isEmpty() && !j1.processus.isEmpty()
					&& cpt < 1000000) {
				cpt++;
				j2.jouer(mem);
				if (!j2.processus.isEmpty()) {
					j1.jouer(mem);
				}
			}
		}

		if (j1.processus.isEmpty()) {
			// Si le processus de J1 est vide, alors J2 a gagné.
			synchronized (score) {
				score[2]++;
			}
		}

		else if (j2.processus.isEmpty()) {
			// Si le processus de J2 est vide, J1 a gagné.
			synchronized (score) {
				score[1]++;
			}
		} else {
			// Si les deux processus sont non vides, il y a égalité.
			synchronized (score) {
				score[0]++;
			}
		}
	}
}
