package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Affiche un message de résultat de fin de partie
 *
 */
public class FinalMsg extends JFrame implements ActionListener {

	/**
	 message d'indication de la fin de la manche 
	 */
	private static final long serialVersionUID = 1L;
	JButton ok;

	/**
	 * @param txt le résultat à afficher
	 */
	public FinalMsg(String txt) {
		super();
		build(txt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == ok)
			this.setVisible(false);	// la fenêtre se ferme quand on clique sur "ok"
	}

	private void build(String txt) {
		setTitle("Affrontement terminé");	// titre de la fenêtre
		setSize(350, 100);					// taille
		setResizable(false);				// dimensions fixes
		setLocationRelativeTo(null);		// positionnement central dans la fenêtre

		// on ne peut pas fermer la fenêtre en cliquant sur la croix rouge à droite
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// le texte dépend de l'issu de la manche
		setContentPane(buildContentPane(txt));
	}

	private JPanel buildContentPane(String s) {

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.white);	// arière plan blanc

		JLabel label = new JLabel(s);

		panel.add(label);

		ok = new JButton("Terminé");		// bouton "Terminé"
		ok.addActionListener(this);
		panel.add(ok);

		return panel;
	}
}
