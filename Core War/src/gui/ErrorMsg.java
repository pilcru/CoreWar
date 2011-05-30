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
 * Affiche un message d'erreur, avec sortie obligatoire par le bouton OK...
 *
 */
public class ErrorMsg extends JFrame implements ActionListener {

/**
Message d'erreur dans une fen�tre � fond rouge
 */
	private static final long serialVersionUID = 1L;
	JButton ok;

	/**
	 * @param txt le message � �mettre
	 */
	public ErrorMsg(String txt) {
		super();
		build(txt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == ok)
			// la fen�tre disparait lorsqu'on clique sur "ok"
			this.setVisible(false);
	}

	private void build(String txt) {
		setTitle("Une erreur s'est produite");	//titre de la fen�tre
		setSize(350, 100);						// dimensions
		setResizable(false);					// taille fixe
		setLocationRelativeTo(null);			// position centrale sur l'�cran

		// la fen�tre ne peut pas �tre ferm�e en utilisant la croix rouge en haut � droite
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// texte affich�e modifiable selon les situations
		setContentPane(buildContentPane(txt));	 
	}

	private JPanel buildContentPane(String s) {

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.red);			// arri�re plan rouge

		JLabel label = new JLabel(s);

		panel.add(label);

		ok = new JButton("ok");					// bouton "ok"
		ok.addActionListener(this);
		panel.add(ok);

		return panel;
	}
}
