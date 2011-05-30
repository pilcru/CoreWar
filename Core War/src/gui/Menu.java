package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

public class Menu extends JMenuBar implements ActionListener {

	private JMenuBar menuBar;
	private JMenuItem chargerJoueur1;
	private JMenuItem chargerJoueur2;
	private JMenuItem demarrer;
	private JMenuItem pause;
	private JMenuItem redemarrer;
	private JMenuItem vitesse1;
	private JMenuItem vitesse2;
	private JMenuItem vitesse3;
	private JMenuItem vitesse4;
	private JMenuItem paparr;
	private JMenuItem papavt;
	private JMenuItem reinit;
	private static final long serialVersionUID = 1L;
	private Simulateur simu;

	public Menu(Simulateur simu) {

		this.simu = simu;

		menuBar = new JMenuBar();
		menuBar.setVisible(false);

		JMenu chargerJoueur = new JMenu("Charger joueurs");

		setChargerJoueur1(new JMenuItem("Joueur 1"));
		getChargerJoueur1().addActionListener(this);
		chargerJoueur.add(getChargerJoueur1());

		chargerJoueur2 = new JMenuItem("Joueur 2");
		chargerJoueur2.addActionListener(this);
		chargerJoueur.add(chargerJoueur2);

		menuBar.add(chargerJoueur);

		ButtonGroup vitesseGroupe = new ButtonGroup();
		JMenu vitesse = new JMenu("Vitesse");

		vitesse1 = new JRadioButtonMenuItem("lent");
		vitesse1.setSelected(true);
		vitesse1.addActionListener(this);
		simu.setSpeed(1);
		vitesseGroupe.add(vitesse1);
		vitesse.add(vitesse1);
		vitesse2 = new JRadioButtonMenuItem("moyen");
		vitesse2.addActionListener(this);
		vitesseGroupe.add(vitesse2);
		vitesse.add(vitesse2);
		vitesse3 = new JRadioButtonMenuItem("rapide");
		vitesse3.addActionListener(this);
		vitesseGroupe.add(vitesse3);
		vitesse.add(vitesse3);
		vitesse4 = new JRadioButtonMenuItem("Terminer le match");
		vitesse4.addActionListener(this);
		vitesseGroupe.add(vitesse4);
		vitesse.add(vitesse4);

		menuBar.add(vitesse);

		demarrer = new JMenuItem("Démarrer");
		demarrer.addActionListener(this);
		menuBar.add(demarrer);

		pause = new JMenuItem("Pause");
		pause.addActionListener(this);
		pause.setVisible(false);
		menuBar.add(pause);

		redemarrer = new JMenuItem("Redémarrer");
		redemarrer.addActionListener(this);
		redemarrer.setVisible(false);
		menuBar.add(redemarrer);

		paparr = new JMenuItem("En arrière");
		paparr.addActionListener(this);
		menuBar.add(paparr);

		papavt = new JMenuItem("En avant");
		papavt.addActionListener(this);
		menuBar.add(papavt);

		reinit = new JMenuItem("Réinitialisation");
		reinit.addActionListener(this);
		menuBar.add(reinit);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == getChargerJoueur1()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Load ld = new Load(1, simu);
					ld.setVisible(true);
				}
			});
		}

		else if (source == chargerJoueur2) {
			if (simu.getNombreJoueurs() == 0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ErrorMsg Err1 = new ErrorMsg(
								"Veuillez charger le joueur 1 en premier.");
						Err1.setVisible(true);
					}
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Load ld = new Load(2, simu);
						ld.setVisible(true);
					}
				});
				chargerJoueur2.setVisible(false);
			}

		} else if (source == demarrer) {
			simu.start();
			demarrer.setVisible(false);
			pause.setVisible(true);

		} else if (source == pause) {
			simu.pause();
			simu.affm.pause(simu.getDelay());
			redemarrer.setVisible(true);
			pause.setVisible(false);
		}

		else if (source == redemarrer) {
			simu.restart();
			redemarrer.setVisible(false);
			pause.setVisible(true);
		}

		else if (source == vitesse1) {
			simu.setSpeed(1);
			simu.affm.actualiseur.setDelay(10);
		}

		else if (source == vitesse2) {
			simu.setSpeed(2);
			simu.affm.actualiseur.setDelay(1);
		}

		else if (source == vitesse3) {
			simu.setSpeed(3);
			simu.affm.actualiseur.setDelay(1);
		}

		else if (source == vitesse4) {
			simu.setSpeed(4);
			while (simu.isRunning()) {
				simu.affm.actualiseur.pauseActualiseur(100);
			}
			simu.pause();
		}

		else if (source == papavt) {
			simu.enAvant();
		}

		else if (source == paparr) {
			try {
				simu.reinitialiser(simu.affm.archive.get(), false);
			} catch (NoSuchElementException ex) {
			}
		}

		else if (source == reinit) {
			simu.pause();
			simu.reinitialiser(new Situation(), true);
			getChargerJoueur1().setVisible(true);
			chargerJoueur2.setVisible(true);
			demarrer.setVisible(true);
			redemarrer.setVisible(false);
			pause.setVisible(false);
		} else {
		}
	}

	public JMenuBar build() {
		menuBar.setVisible(true);
		return menuBar;
	}

	public JMenuItem getPause() {
		return pause;
	}

	public JMenuItem getDemarrer() {
		return demarrer;
	}

	public JMenuItem getRedemarrer() {
		return redemarrer;
	}

	public void setChargerJoueur1(JMenuItem chargerJoueur1) {
		this.chargerJoueur1 = chargerJoueur1;
	}

	public JMenuItem getChargerJoueur1() {
		return chargerJoueur1;
	}
}