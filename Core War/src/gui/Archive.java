package gui;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;
/**
Archive est la base de donnée regroupant les dernières situations du jeu
pour pouvoir revenir en arrière. L'archive est une pile.
Seule un nombre fixe de situations sont conservées. 
 */
public class Archive {
	private LinkedList<Situation> map;	// contenant des dernières situations
	int taille;							// nombre maximum de situation

	public Archive(int taille) {
		map = new LinkedList<Situation>();
		this.taille = taille;
	}

	/**
	 * Ajoute une situation dans l'archive, sans dépasser sa taille maximale
	 */
	public void archiver(Situation situ) {
		int size = map.size();
		// si la pile est pleine, on supprime la plus ancienne situation
		if (size == taille) {		
			map.addFirst(situ);
			map.pollLast(); // situations jetée
		} else {
			//on ajoute la dernière situation en date sur la haut de la pile
			map.addFirst(situ); 
		}
	}

	/**
	 * @return la dernière situation mémoire sauvegardée et la supprime de l'archive
	 * @throws NoSuchElementException si l'archive est vide
	 */
	public Situation get() throws NoSuchElementException {
		if (!map.isEmpty()) {
			// on récupère le haut de la pile et la supprimant de l'archive
			return map.pollFirst(); 
		} else {
			// si la pile est vide, on envoie un message d'erreur et une exception
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ErrorMsg Err1 = new ErrorMsg(
							"Retour supplémentaire impossible.");
					Err1.setVisible(true);
				}
			});
			throw new NoSuchElementException();
		}
	}
	/**
	 * @return la dernière situation mémoire sauvegardée sans la supprimer de l'archive
	 */
	public Situation nextElement() {
		return map.element();
	}

	public String toString() {
		String s = "";
		for (Situation situ : map) {
			s = s + "\n" + situ.toString();
		}
		return s + "\n" + map.size();
	}
}
