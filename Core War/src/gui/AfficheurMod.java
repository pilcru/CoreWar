package gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Adresse;

/**
 *	La classe principale de la gui
 *	Comprend notamment un actualiseur au taux de rafraichissement réglable
 *	la représentation graphique de la mémoire
 *	la création de l'espace de simulation
 */
public class AfficheurMod extends JFrame {

	/**
	 * L'Actualiseur permet de rafraîchir l'affichage graphique avec un taux de rafraichissement modifiable à la volée
	 */
	class Actualiseur implements Runnable {
		private boolean running; // permet de contrôler l'atat actuel de l'actualiseur (rafraichissement actif ou inactif)
		private long delay; // le taux de rafraichissement en ms

		public Actualiseur() {
			delay = 10; // paramètre par défaut, utilisé au lancement de la gui
		}

		/**
		 * Interrompt l'actualiseur pendant un temps delay
		 * @param delay
		 */
		public void pauseActualiseur(long delay) {
			running = false;
			boolean ok = false;
			boolean interrupted = false;
			while (!ok) {
				try {
					Thread.sleep(delay); // tant qu'on n'a pas réussi à attendre pendant delay, on réessaie
					ok = true;
				} catch (InterruptedException e) {
					interrupted = true; // on éliminera le thread plus tard, il faut encore attendre
				}
			}
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}

		// Modifier la fréquence d'actualisation
		public void setDelay(long delay) {
			this.delay = delay;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 * Lance le rafraichissment, au taux this.delay
		 */
		@Override
		public void run() {
			while (running) {
				actualiser();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		// Démarrage du Thread.
		public void start() {
			Thread a = new Thread(null, this);
			running = true;
			a.start();
		}

		// Arrêt du Thread
		public void interrupt() {
			Thread.currentThread().interrupt();
		}
	}

	// La fenêtre a proprement parler
	private static final long serialVersionUID = 1L;
	public final Actualiseur actualiseur = new Actualiseur();
	private Menu menu; // le menu et les boutons
	private Simulateur simu; // l'émuleur de mémoire core war
	public JLabel[] tab; // le tableau des cases mémoire
	private int actif1; // Processus actif du joueur 1 (dernier résultat de j1.getProcess().element().get() enregistré)
	private int actif2; // id pour j2
	public final Object lock = new Object(); // mutex partagé avec simu, pour synchroniser seulement les messagers

	public Archive archive = new Archive(100); // la pile des derniers états, dont on peut éventuellemnt changer la taille ici (minimum 1 pour la coloration)

	/**
	 * Création de l'afficheur et des cases mémoire
	 * actif1 et actif2 n'existent pas encore, sont initialisés à -1
	 */
	public AfficheurMod() {
		super();
		simu = new Simulateur(this);
		tab = new JLabel[800];			// 800 est la taille de la mémoire
		
		// Initialisation de l'affichage
		for (int i = 0; i < 800; i++) {
			String s = "<html><div style=\"width:70; height:13; border:solid 1px black; text-align: center;\"><font face = \"Courier New\" size = \"2\" color = \""
					+ "black"
					+ "\">"
					+ simu.getOp(new Adresse(i))
					+ "</font></div></html>";
			tab[i] = new JLabel(s);
			tab[i].setOpaque(true); // sans ça pas de couleur de fond visible
		}
		actif1 = -1;
		actif2 = -1;
		build();
	}

	/**
	 * Actualise l'affichage de la mémoire et recolorie les cases
	 * On se synchronise avec le messager partagé avec simu via lock
	 */
	public void actualiser() {
		// l'actualisation de l'affichage est synchronisée avec le simulateur
		synchronized (lock) {
			for (int i = 0; i < 800; i++) {
				String s = "<html><div style=\"width:70; height:13; border:solid 1px black; text-align: center;\"><font face = \"Courier New\" size = \"2\" color = \""
						+ "black"
						+ "\">"
						+ simu.getOp(new Adresse(i))
						+ "</font></div></html>";
				tab[i].setText(s);
			}
			if (simu.getProcess(1) != null && !simu.getProcess(1).isEmpty()) { // ce joueur a quelquechose a afficher
				if (actif1 > -1) { // le précédent processus actif est peut-être devenu inactif, on le recolorie
					// la position précedante du joueur 1 est re-marquée en vert
					tab[actif1].setBackground(Color.green); 
				}
				for (Adresse a : simu.getProcess(1)) { // tous ces processus sont a priori inactifs de j1
					tab[a.get()].setBackground(Color.green);
				}
				tab[simu.getProcess(1).element().get()] // la case mémoire du processus actif
						.setBackground(Color.RED); // doit être coloriée
				actif1 = simu.getProcess(1).element().get(); // on met à jour son adresse
			} else if (actif1 > -1) { // le joueur est mort depuis la dernière actualisation, son dernier processus n'est plus actif
				tab[actif1].setBackground(Color.green);
				actif1 = -1;
			}
			if (simu.getNombreJoueurs() == 2) { // un deuxième joueur a été créé, faisons la même chose pour lui...
				if (simu.getProcess(2) != null && !simu.getProcess(2).isEmpty()) {
					
					// recoloriage de l'ancienne position en bleu 
					if (actif2 > -1) {
						tab[actif2].setBackground(Color.blue);
					}
					// marquage des adresses du processus de J2 en bleu
					for (Adresse a : simu.getProcess(2)) {
						tab[a.get()].setBackground(Color.blue);
					}
					
					// la position de J2 est marquée en rouge
					tab[simu.getProcess(2).element().get()]
							.setBackground(Color.RED);
					actif2 = simu.getProcess(2).element().get();
				}
			} else if (actif2 > -1) {
				// à la mort de J2, sa dernière position est effacée
				tab[actif2].setBackground(Color.blue);
				actif2 = -1;
			}
		}
	}

	/**
	 * Construction de la fenêtre graphique
	 */
	private void build() {
		menu = new Menu(simu);
		setJMenuBar(menu.build());
		setTitle("Core War");
		setSize(1250, 980); // taille standard pour un affichage 16*50 cases mémoire, ne pas changer
		setResizable(false);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		setContentPane(buildContentPanel());
	}

	/**
	 * Construction du tableau d'affichage des cases mémoire
	 * @return le JPanel construit
	 */
	private JPanel buildContentPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.white); 	// fond blanc
		for (int i = 0; i < 800; i++) { 	
			panel.add(tab[i]);			// ajout des cases représentant la mémoire
		}

		return panel;
	}

	public Menu getMenu() {
		return menu;
	}

	/**
	 * Stoppe l'actualisation de l'affichage pendant un temps delay
	 * @param delay
	 */	public void pause(long delay) {
		actualiseur.pauseActualiseur(delay);
		actualiseur.start();
	}

}
