package arbitre;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import core.Operation;

/**
	Un tournoi comporte un nombre N de joueurs.
	Chaque joueur joue un battle avec chaque autre joueur.
 */
public class Tournoi {

	static boolean verbose = true;

	private static volatile Scanner scanner = scanner(new InputStreamReader(
			System.in));

	public static void main(String[] args) {
		Integer N = 0;
		ArrayList<LinkedList<Operation>> joueurs = new ArrayList<LinkedList<Operation>>(
				N);
		// Le nombre de joueurs ainsi que leurs codes peuvent être passé en arguments
		// sinon ils sont demandés par le programme dans la console.
		
		if (args.length == 0) {
			while (N == 0) {
				if (verbose)
					System.out
							.print("Combien y a-t-il de joueurs dans ce tournoi ?\nEntrez un entier strictement positif :");
				N = scanner.nextInt();
			}
			// il faut sauter la ligne qui contenait N avant de continuer
			scanner.nextLine();
			for (int i = 0; i < N; i++) {
				while (joueurs.size() < i + 1) {
					if (verbose)
						System.out
								.print("Emplacement du fichier RedCode pour le joueur "
										+ i + 1 + " ? : ");
					try {
						// Un joueur est créé à partir du fichier indiqué dans la console.
						joueurs.add(i, Interpret.interpret(scanner.nextLine()));
					} catch (FileNotFoundException e) {
					}
				}
			}
		} else {
			N = args.length;
			for (int i = 0; i < N; i++) {
				try {
					joueurs.add(i, Interpret.interpret(args[i]));
				} catch (FileNotFoundException e) {
					return;
				}
			}
		}

		int[] total = new int[N];

		// quand il y a plus de 100 joueurs, le troisième chiffre décale tout...
		if (N > 1 && N < 100) {
			String s = "s";
			if (verbose)
				System.out.println("Un tournoi à " + N + " joueur" + s
						+ " a commencé");
			s = "s";
			
			// Ecriture du tableau des résultats (en tête)
			
			String tableauResulats = "+";
			for (int i = 0; i < N; i++) {
				tableauResulats += "------------";
			}
			tableauResulats += "-----------+\n|";
			for (int i = 0; i < N * 6 - 5; i++) {
				tableauResulats += " ";
			}
			tableauResulats += "Tableau des résultats";
			for (int i = 0; i < N * 6 - 5; i++) {
				tableauResulats += " ";
			}
			tableauResulats += "|\n|";
			for (int i = 0; i < N * 6 - 7; i++) {
				tableauResulats += " ";
			}
			tableauResulats += "i gagne/j gagne/egalités";
			for (int i = 0; i < N * 6 - 6; i++) {
				tableauResulats += " ";
			}
			tableauResulats += "|\n+";
			for (int i = 0; i < N; i++) {
				tableauResulats += "-----------+";
			}
			tableauResulats += "-----------+\n|";
			tableauResulats += " i   \\   j |";
			for (int i = 0; i < N; i++) {
				tableauResulats += " Joueur "
						+ ((i + 1) < 10 ? "0" + (i + 1) : (i + 1)) + " |";
			}
			for (int i = 0; i < N; i++) {
				tableauResulats += "\n+";
				for (int j = 0; j < N; j++) {
					tableauResulats += "-----------+";
				}
				tableauResulats += "-----------+\n";
				tableauResulats += "| Joueur "
						+ ((i + 1) < 10 ? "0" + (i + 1) : (i + 1)) + " |";
				for (int j = 0; j < i + 1; j++) {
					tableauResulats += "    xxx    |";
				}
				
				// Execution de tous les battles, inscription des résultats
				for (int j = i + 1; j < N; j++) {
					Battle battle = new Battle(joueurs.get(i), joueurs.get(j));
					int[] score = battle.start();
					tableauResulats += "  "
							+ (score[1] < 10 ? "0" + score[1] : score[1]) + "/"
							+ (score[2] < 10 ? "0" + score[2] : score[2]) + "/"
							+ (score[0] < 10 ? "0" + score[0] : score[0])
							+ " |";
					total[i] += 3 * score[1] + score[0];
					total[j] += 3 * score[2] + score[0];
					if (verbose)
						System.out.println("Match "
								+ ((j - i) + i * (2 * N - i - 1) / 2) + "/"
								+ (N * (N - 1) / 2) + " terminé");
				}
			}
			
			// Ecriture du tableau de résultats (pied)
			tableauResulats += "\n+";
			for (int i = 0; i < N; i++) {
				tableauResulats += "-----------+";
			}
			tableauResulats += "-----------+\n";
			System.out.println(tableauResulats);
			String tableauScore = "+--------------------+\n";
			tableauScore += "| Tableau des scores |\n";
			tableauScore += "+-----------+--------+\n";
			for (int i = 0; i < N; i++) {
				tableauScore += "| Joueur "
						+ ((i + 1) < 10 ? "0" + (i + 1) : (i + 1))
						+ " |  "
						+ (total[i] < 100 ? (total[i] < 10 ? "00" + total[i]
								: "0" + total[i]) : total[i]) + "   |\n";
				tableauScore += "+-----------+--------+\n";
			}
			System.out.println(tableauScore);
			HashSet<Integer> gagnants = trouverGagnants(total);
			System.out.print("Le" + (gagnants.size() > 1 ? "s" : "")
					+ " gagnant"
					+ (gagnants.size() > 1 ? "s sont :" : " est :"));
			for (Integer i : gagnants) {
				System.out.print(" Joueur " + (i + 1));
			}
			System.out.println(".");
		} else if (N == 1) {
			System.out.println("Le joueur 01 a gagné par forfait !");
		} else if (N == 0) {
			System.out.println("Tout ça pour ça !..");
		} else if (N > 99) {
			for (int i = 0; i < N; i++) {
				for (int j = i + 1; j < N; j++) {
					Battle battle = new Battle(joueurs.get(i), joueurs.get(j));
					int[] score = battle.start();
					total[i] += 3 * score[1] + score[0];
					total[j] += 3 * score[2] + score[0];
				}
			}
			HashSet<Integer> gagnants = trouverGagnants(total);
			System.out.print("Le" + (gagnants.size() > 1 ? "s" : "")
					+ " gagnant"
					+ (gagnants.size() > 1 ? "s sont :" : " est :"));
			for (Integer i : gagnants) {
				System.out.print(" Joueur " + (i + 1));
			}
			System.out.println(".");
		}
	}

	private static Scanner scanner(Reader in) {
		Scanner scanner = new Scanner(in);
		return scanner;
	}

	// retourne le numéro des joueurs ayant remporté le tournoi
	private static HashSet<Integer> trouverGagnants(int[] total) {
		int max = 0;
		int N = total.length;
		HashSet<Integer> gagnants = new HashSet<Integer>();
		for (int i = 0; i < N; i++) {
			if (total[i] > max) {
				max = total[i];
			}
		}
		for (int i = 0; i < N; i++) {
			if (total[i] == max) {
				gagnants.add(i);
			}
		}
		return gagnants;
	}

}
