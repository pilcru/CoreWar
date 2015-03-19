package gui;

import java.awt.Color;
import java.util.LinkedList;
import core.Adresse;
import core.Joueur;
import core.Memoire;
import core.Operation;

public class Situation {
	Memoire mem;
	Joueur j1;
	Joueur j2;
	int tour;
	Color[] couleur;

	public Situation() {
		mem = new Memoire("");
		tour = 1;
		couleur = new Color[800];
		for (int i = 0; i < 800; i++) {
			couleur[i] = Color.white;
		}
	}

	public Situation(Memoire mem, Joueur j1, Joueur j2, int tour,
			AfficheurMod affm) {
		this.mem = mem.clone();
		if (j1 != null) {
			this.j1 = new Joueur(new LinkedList<Operation>());
			this.j1.processus = new LinkedList<Adresse>(j1.processus);
		}
		if (j2 != null) {
			this.j2 = new Joueur(new LinkedList<Operation>());
			this.j2.processus = new LinkedList<Adresse>(j2.processus);
		}
		this.tour = tour;
		couleur = new Color[800];
		for (int i = 0; i < 800; i++) {
			couleur[i] = affm.tab[i].getBackground();
		}
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 800; i++) {
			s = s + i + " : " + mem.O[i].toString() + " | ";
		}
		s = s + j1.toString() + " | ";
		if (j2 != null)
			s = s + j2.toString();
		return s;
	}

}