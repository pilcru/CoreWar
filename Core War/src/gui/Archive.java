package gui;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;
/**
Archive est la base de donn�e regroupant les derni�res situations du jeu
pour pouvoir revenir en arri�re. L'archive est une pile.
Seule un nombre fixe de situations sont conserv�es. 
 */
public class Archive {
	private LinkedList<Situation> map;	// contenant des derni�res situations
	int taille;							// nombre maximum de situation

	public Archive(int taille) {
		map = new LinkedList<Situation>();
		this.taille = taille;
	}

	/**
	 * Ajoute une situation dans l'archive, sans d�passer sa taille maximale
	 */
	public void archiver(Situation situ) {
		int size = map.size();
		// si la pile est pleine, on supprime la plus ancienne situation
		if (size == taille) {		
			map.addFirst(situ);
			map.pollLast(); // situations jet�e
		} else {
			//on ajoute la derni�re situation en date sur la haut de la pile
			map.addFirst(situ); 
		}
	}

	/**
	 * @return la derni�re situation m�moire sauvegard�e et la supprime de l'archive
	 * @throws NoSuchElementException si l'archive est vide
	 */
	public Situation get() throws NoSuchElementException {
		if (!map.isEmpty()) {
			// on r�cup�re le haut de la pile et la supprimant de l'archive
			return map.pollFirst(); 
		} else {
			// si la pile est vide, on envoie un message d'erreur et une exception
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ErrorMsg Err1 = new ErrorMsg(
							"Retour suppl�mentaire impossible.");
					Err1.setVisible(true);
				}
			});
			throw new NoSuchElementException();
		}
	}
	/**
	 * @return la derni�re situation m�moire sauvegard�e sans la supprimer de l'archive
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
