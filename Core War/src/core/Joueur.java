package core;

import java.util.LinkedList;

/**
La classe joueur encapsule deux listes :
"processus" repr�sente la file des adresses (les processus) auxquelles va se rendre le joueur,
operation repr�sente le programme initial que le joueur recopie dans la m�moire au premier tour.

La fonction jouer lui fait executer un tour de jeu.
 */
public class Joueur {
	public LinkedList<Adresse> processus;
	public LinkedList<Operation> operations;

	public Joueur(LinkedList<Operation> lo) {
		processus = new LinkedList<Adresse>();
		this.operations = lo;
	}

	
	// Un tour de jeu
	
	public void jouer(Memoire mem) {
		// L'adresse consid�r�e est la premi�re de la file de processus.
		Adresse p = processus.poll();
		// L'instruction de l'op�ration correspondante d�termine l'action a suivre
		Instruction i = mem.O[p.get()].ins;
		// p2 est l'adresse o� est envoy� le joueur.
		Adresse p2 = mem.executer(p);
		if (i == Instruction.DAT) {
		} else if (i == Instruction.SPL) {
			// pour l'instruction SPL, on rajoute un deuxi�me processus � la liste
			processus.addLast(p.plus(new Adresse(1)));
			if (processus.size() < 200) {
				processus.addLast(p2);
			}
		} else {
			// sauf pour une instruction DAT, on ajoute l'adresse renvoy�e par "executer" a la liste des procesus
			processus.addLast(p2);
		}

	}

}
