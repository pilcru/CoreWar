package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import arbitre.Interpret;
import core.Operation;

public class Load extends JFrame implements ActionListener {

	/**
	 Fenêtre permettant de créer les joueurs en créant leur code de base
	 */
	private static final long serialVersionUID = 1L;
	private JButton charger;		//bouton "charger" pour récupérer un fichier
	private JTextField zoneadr;		//zone d'écriture de l'emplacement du fichier
	private JTextArea zoneRedCode;	//zone d'affichage et de modification du fichier chargé
	private String redcode = "";	//texte s'incrivant dans la fenêtre zoneRedCode
	private JButton compiler;		//complie le texte de la fenêtre zoneRedCode
	private JButton creer;			//crée le joueur à partir du texte compilé
	private JButton abandonner;		//revenir à l'afficheur sans créer de joueur
	private JTextArea process;		//affichage du code compilé
	private int joueur;				//joueur (1 ou 2) qui va être créé
	private Simulateur simu;		//simulateur dans lequel on inscrit le code créé
	// liste à créer, à associer au joueur et à inscrire dans le simulateur
	LinkedList<Operation> lo = new LinkedList<Operation>();

	public Load(int joueur, Simulateur simu) {
		super();
		this.joueur = joueur;
		this.simu = simu;
		build();
	}

	// actions associées à chaque bouton
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == charger) {
			String chaine = "";
			redcode = zoneadr.getText();
			// récupération du fichier selon le chemin indiqué dans la fenêtre "zoneadr"
			try {
				InputStream ips = new FileInputStream(zoneadr.getText());
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader br = new BufferedReader(ipsr);
				String ligne;
				while ((ligne = br.readLine()) != null) {
					chaine += ligne + "\n";
				}
				br.close();
				
			//si le chemin n'est pas bon, on envoie un message d'erreur
			} catch (FileNotFoundException e1) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ErrorMsg Err1 = new ErrorMsg("Fichier introuvable.");
						Err1.setVisible(true);
					}
				});

			} catch (IOException e1) {
			}
			//inscription du texte récupéré dans la fenêtre "zoneRedCode"
			zoneRedCode.setText(chaine);
		}

		// Interprète le texte RedCode brut et affiche le résultat du toString() sur la liste d'opérations obtenues
		else if (source == compiler) {
			if (!zoneRedCode.getText().equals("")) {
				process.setVisible(true); //affichage du bouton
			}
			lo = Interpret.interpret(zoneRedCode.getText(), true);
			String compile = "";
			for (Operation o : lo) {
				compile += o.toString() + "\n";
			}
			process.setText(compile);
			if (lo.size() > 0) {
				creer.setVisible(true);
			}

		// Crée le joueur dans simu à partir de la liste d'opérations obtenue
		} else if (source == creer) {
			this.setVisible(false);
			simu.creerJoueur(joueur, lo);
			if(simu.getNombreJoueurs()>0)
				simu.affm.getMenu().getChargerJoueur1().setVisible(false);
		}

		else if (source == abandonner) {
			this.setVisible(false);
		}

	}

	private void build() {
		setTitle("Charger un joueur");
		setSize(600, 700);
		setResizable(false);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setContentPane(buildContentPane());
	}

	private JPanel buildContentPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.darkGray);

		JLabel label = new JLabel("");
		panel.add(label);

		zoneadr = new JTextField(50);
		panel.add(zoneadr);

		charger = new JButton("Charger fichier");
		charger.addActionListener(this);
		panel.add(charger);

		zoneRedCode = new JTextArea(10, 50);
		zoneRedCode.setText(redcode.toString());
		JScrollPane scrollingRedCode = new JScrollPane(zoneRedCode);
		panel.add(scrollingRedCode);

		compiler = new JButton("Compiler");
		compiler.addActionListener(this);
		panel.add(compiler);
		compiler.setVisible(true);

		process = new JTextArea(10, 50);
		panel.add(process);
		JScrollPane scrollingProcess = new JScrollPane(process);
		panel.add(scrollingProcess);

		creer = new JButton("Creer Joueur" + (joueur == 1 ? " 1" : " 2"));
		creer.addActionListener(this);
		panel.add(creer);
		creer.setVisible(false);

		abandonner = new JButton("Abandonner");
		abandonner.addActionListener(this);
		panel.add(abandonner);

		return panel;
	}

	public LinkedList<Operation> getList() {
		return lo;
	}
}
