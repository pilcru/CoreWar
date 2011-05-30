package gui;

import javax.swing.SwingUtilities;

/**
cette classe lance l'afficheur, et donc le jeu
 */
public final class Gui {

	public static void main(String[] Args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AfficheurMod affm = new AfficheurMod(); //construction de l'afficheur
				affm.setVisible(true);					//lancement de l'afficheur
			}
		});
	}
}
