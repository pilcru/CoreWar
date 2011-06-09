package arbitre;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import core.Adresse;
import core.Instruction;
import core.Operation;

/**
 * Permet d'interpréter et compiler un fichier .asm contenant un warrior.
 */
public final class Interpret {

	/**
	 * @param fileName
	 *            le chemin du fichier à compiler
	 * @return une HashMap<String,Integer> reliant les étiquettes à la position
	 *         absolue de leur déclaration dans le fichier
	 * @throws FileNotFoundException
	 *             si le fichier est introuvable à l'emplacement spécifié
	 */
	private static HashMap<String, Integer> getLabels(String fileName)
			throws FileNotFoundException {

		String instructionPattern = "(?i)(DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL)";
		int i = 0; // permet de compter la position absolue dans le fichier,
					// commentaires non compris.
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		try {
			Scanner scanner = new Scanner(new FileReader(fileName));

			while (scanner.hasNextLine()) { // on va donc lire ligne par ligne
				String line = formatSpaces(scanner.nextLine());
				Scanner lineScanner = new Scanner(line);
				
				if (line.startsWith(";")) {
					// c'est un commentaire, donc rien à faire et i ne change
					// pas
				}
				
				else if (lineScanner.hasNext(Pattern
						.compile(instructionPattern))) {
					// il n'y a pas d'étiquette, mais on ajoutera quand même une
					// ligne donc i change
					i++;
				}

				else {
					String label;
					try {
						label = lineScanner.next();
						labels.put(label, i);
						i++;
					} catch (NoSuchElementException e) {
						if (line.matches("[ \t\n\f\r]+")||line.equals("")) {
							// il y a des lignes vides à la fin du fichier
						} else {
							System.err
							.println("cette ligne ne contient pas les éléments nécessaires : "
									+ line);
						}
					}
				}
			}

			scanner.close();

		} catch (FileNotFoundException f) {
			throw f;
		}

		return labels;
	}

	/**
	 * Même méthode qu'au dessus mais String in est le texte brut d'un warrior
	 * non compilé b ne sert à rien d'autre qu'à surcharger la méthode.
	 * 
	 * @param in
	 * @param b
	 * @return
	 */
	private static HashMap<String, Integer> getLabels(String in, boolean b) {

		String instructionPattern = "(?i)(DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL)";
		int i = 0;
		HashMap<String, Integer> labels = new HashMap<String, Integer>();
		Scanner scanner = new Scanner(in);

		while (scanner.hasNextLine()) {
			String line = formatSpaces(scanner.nextLine());
			Scanner lineScanner = new Scanner(line);

			if (line.startsWith(";")) {
			}

			else if (lineScanner.hasNext(Pattern.compile(instructionPattern))) {
				i++;
			}

			else {
				String label;
				try {
					label = lineScanner.next();
					labels.put(label, i);
					i++;
				} catch (NoSuchElementException e) {
					if (line.matches("[ \t\n\f\r]+")||line.equals("")) {
						// il y a des lignes vides à la fin du fichier
					} else {
						System.err
						.println("cette ligne ne contient pas les éléments nécessaires : "
								+ line);
					}
				}
			}
		}

		scanner.close();
		return labels;
	}

	/**
	 * Couche supérieure de l'interprète, ne fait que lire une par une les
	 * lignes du fichier avant de les envoyer à l'interprète proprement dit
	 * 
	 * @param fileName
	 *            le chemin du fichier à lire
	 * @return une LinkedList<Operation> représentant le joueur
	 * @throws FileNotFoundException
	 *             si le fichier est introuvable à l'emplacement spécifié
	 */
	public static LinkedList<Operation> interpret(String fileName)
			throws FileNotFoundException {

		LinkedList<Operation> list = new LinkedList<Operation>();
		int i = 0; // permet de retenir la position absolue de la ligne dans le
					// fichier, commentaires non inclus

		try {
			HashMap<String, Integer> labels = getLabels(fileName);
			Scanner scanner = new Scanner(new FileReader(fileName));

			while (scanner.hasNextLine()) {
				Operation o = processLine(formatSpaces(scanner.nextLine()), labels, i); // appel
																			// de
																			// l'interprète
																			// à
																			// proprement
																			// parler
				if (o != null) { // ce n'est pas une ligne de commentaire
					list.add(o); // donc on ajoute l'operation au joueur
					i++; // et on est descendu d'une ligne réelle
				}
			}
			scanner.close();

		} catch (FileNotFoundException f) {
			System.err.println("Impossible d'ouvrir ce fichier : " + fileName);
			throw f;
		}

		return list;
	}

	/**
	 * Même méthode qu'au dessus mais in est le texte brut du warrior
	 * 
	 * @param in
	 * @param b
	 * @return
	 */
	public static LinkedList<Operation> interpret(String in, boolean b) {

		LinkedList<Operation> list = new LinkedList<Operation>();
		int i = 0;

		HashMap<String, Integer> labels = getLabels(in, b);
		Scanner scanner = new Scanner(in);

		while (scanner.hasNextLine()) {
			Operation o = processLine(formatSpaces(scanner.nextLine()), labels, i);
			if (o != null) {
				list.add(o);
				i++;
			}
		}
		return list;
	}

	/**
	 * Le coeur de l'interprète.
	 * 
	 * @param line
	 *            la ligne à interpréter
	 * @param labels
	 *            les étiquettes du fichier et leurs positions absolues
	 * @param i
	 *            la position absolue de la ligne lue
	 * @return une opération à ajouter au warrior
	 */
	private static Operation processLine(String line,
			HashMap<String, Integer> labels, int i) {
		if (line.startsWith(";")) { // c'est une ligne de commentaire
			return null;
		}

		else {
			Scanner scanner = new Scanner(line);
			String instructionPattern = "(?i)(DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL)";
			String modePattern = "#|@|<";

			Operation o = new Operation();

			try {
				if (!scanner.hasNext(Pattern.compile(instructionPattern))) {
					// il y a une étiquette, on la saute, l'instruction viendra
					// après
					scanner.next();
				}

				// on récpère l'instruction
				String ins = scanner.next();
				if (ins.equals("DAT") || ins.equals("dat")) {
					o.ins = Instruction.DAT;
				} else if (ins.equals("MOV") || ins.equals("mov")) {
					o.ins = Instruction.MOV;
				} else if (ins.equals("ADD") || ins.equals("add")) {
					o.ins = Instruction.ADD;
				} else if (ins.equals("SUB") || ins.equals("sub")) {
					o.ins = Instruction.SUB;
				} else if (ins.equals("JMP") || ins.equals("jmp")) {
					o.ins = Instruction.JMP;
				} else if (ins.equals("JMZ") || ins.equals("jmz")) {
					o.ins = Instruction.JMZ;
				} else if (ins.equals("JMN") || ins.equals("jmn")) {
					o.ins = Instruction.JMN;
				} else if (ins.equals("CMP") || ins.equals("cmp")) {
					o.ins = Instruction.CMP;
				} else if (ins.equals("SLT") || ins.equals("slt")) {
					o.ins = Instruction.SLT;
				} else if (ins.equals("DJN") || ins.equals("djn")) {
					o.ins = Instruction.DJN;
				} else if (ins.equals("SPL") || ins.equals("spl")) {
					o.ins = Instruction.SPL;
				} else {
					System.out
							.println("Instruction invalide : \"" + ins + "\"");
				}

				// -----------------------------
				// I) Récupération de l'opérande A

				// 1) récupération du mode de l'opérande A
				String mode1 = scanner.findWithinHorizon(
						Pattern.compile(modePattern), 2);
				if (mode1 != null) {
					// si le mode est indiqué, on cherche lequel est-ce
					if (mode1.equals("#")) {
						o.OA.m = "#";
					} else if (mode1.equals("@")) {
						o.OA.m = "@";
					} else if (mode1.equals("<")) {
						o.OA.m = "<";
					}
				}
				// sinon c'est direct
				else {
					o.OA.m = "_";
				}

				// 2) récupération de l'adresse de l'opérande A
				// on veut récupérer l'adresse toute seule même si elle est
				// collée à une ","
				scanner.useDelimiter(",|[ \t]");
				if (!scanner.hasNextInt()) {
					// c'est une etiquette
					String label = scanner.next();
					if (labels.get(label) == null) {
						System.err.println("Etiquette introuvable : \"" + label
								+ "\"");
					} else {
						o.OA.a = new Adresse(labels.get(label) - i);
					}
				} else {
					o.OA.a = new Adresse(scanner.nextInt());
				}

				// on revient en mode normal
				scanner.useDelimiter("[ \t]");

				// ---------------------------------
				// II) Récupération de l'opérande B

				// s'il y a une ",", il y a un deuxième opérande, sinon non
				if (",".equals(scanner.findWithinHorizon(",", 1))) {
					// la recherhce de la virgule place le scanner derrière
					// elle...

					// 1) récupération du mode de l'opérande B
					String mode2 = scanner.findWithinHorizon(
							Pattern.compile(modePattern), 2);
					if (mode2 != null) {
						if (mode2.equals("#")) {
							o.OB.m = "#";
						} else if (mode2.equals("@")) {
							o.OB.m = "@";
						} else if (mode2.equals("<")) {
							o.OB.m = "<";
						}
					} else {
						o.OB.m = "_";
					}

					// 2) récupération de l'adresse de l'opérande B
					if (!scanner.hasNextInt()) {
						// c'est une étiquette
						String label = scanner.next();
						if (labels.get(label) == null) {
							System.err.println("Etiquette introuvable : \""
									+ label + "\"");
						} else {
							o.OB.a = new Adresse(labels.get(label) - i);
						}
					} else {
						o.OB.a = new Adresse(scanner.nextInt());
					}
				}
				return o;

			} catch (NoSuchElementException e) {
				if (line.matches("[ \t\n\f\r]+")||line.equals("")) {
					// il y a des lignes vides à la fin du fichier
				} else {
					System.err
							.println("cette ligne ne contient pas les éléments nécessaires : "
									+ line);
				}
				return null;
			}
		}
	}
	
	/**
	 * Remplace les espaces multiples par un seul et supprime les espaces de début et fin de ligne
	 * @param line la ligne à traiter
	 * @return la chaine de caractères formatée.
	 */
	private static String formatSpaces(String line) {
		String formatted = line.trim();
		formatted = formatted.replaceAll("[ \t]+", " ");
		formatted = formatted.replaceAll("[ \t]+,", ",");
		return formatted;
	}
}