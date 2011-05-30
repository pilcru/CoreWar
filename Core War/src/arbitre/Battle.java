package arbitre;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.Operation;

/**
Un battle repr�sente une s�rie de 40 manches entre deux joueurs, 
en alternant l'odre de jeu entre les joueurs.
Les joueurs et l m�moire sont recr��s pour chaque manche,
la classe Battle ne contient donc que les codes initiaux des deux joueurs.
 */
public class Battle {

	LinkedList<Operation> l1;	// Code initial du joueur 1
	LinkedList<Operation> l2;	// Code initial du joueur 2
	int[] score;				// Tableau des scores. [Egalit�, J1, J2]

	public Battle(LinkedList<Operation> l1, LinkedList<Operation> l2) {

		this.l1 = l1;
		this.l2 = l2;
		score = new int[3];

	}

	public int[] getscore() {
		return score;
	}

	public void printscore() {
		System.out.println("Egalit� :  " + score[0]);
		System.out.println("Joueur 1 : " + score[1]);
		System.out.println("Joueur 2 : " + score[2]);
	}

	public int[] start() {
		// Les diff�rentes manches sont effectu�es par diff�rents Threads de fa�on parall�les.
		// Il y a autant de Threads � tourner simultan�ment
		// qu'il y a de coeurs dans le processeur dan la machine.
		
		score = new int[3];
		ExecutorService pool = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		for (int i = 0; i < 40; i++) {
			pool.execute(new Manche(l1, l2, "", score, i % 2));
		}
		pool.shutdown();
		while (!pool.isTerminated()) {
		}
		return score;
	}

}
