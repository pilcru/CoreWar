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
 * Permet d'interpr�ter et compiler un fichier .asm contenant un warrior.
 */
public final class Interpret {

	/**
	 * @param fileName
	 *            le chemin du fichier � compiler
	 * @return une HashMap<String,Integer> reliant les �tiquettes � la position
	 *         absolue de leur d�claration dans le fichier
	 * @throws FileNotFoundException
	 *             si le fichier est introuvable � l'emplacement sp�cifi�
	 */
	private static HashMap<String, Integer> getLabels(String fileName)
			throws FileNotFoundException {

		String instructionPattern = "DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL";
		int i = 0; // permet de compter la position absolue dans le fichier,
					// commentaires non compris.
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		try {
			Scanner scanner = new Scanner(new FileReader(fileName));

			while (scanner.hasNextLine()) { // on va donc lire ligne par ligne
				String line = scanner.nextLine();
				Scanner lineScanner = new Scanner(line);

//				while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//					scanner.next();
//				}
				
				if (line.startsWith(";")) {
					// c'est un commentaire, donc rien � faire et i ne change
					// pas
				}
				
				else if (lineScanner.hasNext(Pattern
						.compile(instructionPattern))) {
					// il n'y a pas d'�tiquette, mais on ajoutera quand m�me une
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
						if (line.matches("[ \t\n\f\r]")) {
							// il y a des lignes vides � la fin du fichier
						} else {
							System.err
							.println("cette ligne ne contient pas les �l�ments n�cessaires : "
									+ line);
						}
						return null;
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
	 * M�me m�thode qu'au dessus mais String in est le texte brut d'un warrior
	 * non compil� b ne sert � rien d'autre qu'� surcharger la m�thode.
	 * 
	 * @param in
	 * @param b
	 * @return
	 */
	private static HashMap<String, Integer> getLabels(String in, boolean b) {

		String instructionPattern = "DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL";
		int i = 0;
		HashMap<String, Integer> labels = new HashMap<String, Integer>();
		Scanner scanner = new Scanner(in);

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner lineScanner = new Scanner(line);

//			while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//				scanner.next();
//			}

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
					if (line.matches("[ \t\n\f\r]")) {
						// il y a des lignes vides � la fin du fichier
					} else {
						System.err
						.println("cette ligne ne contient pas les �l�ments n�cessaires : "
								+ line);
					}
					return null;
				}
			}
		}

		scanner.close();
		return labels;
	}

	/**
	 * Couche sup�rieure de l'interpr�te, ne fait que lire une par une les
	 * lignes du fichier avant de les envoyer � l'interpr�te proprement dit
	 * 
	 * @param fileName
	 *            le chemin du fichier � lire
	 * @return une LinkedList<Operation> repr�sentant le joueur
	 * @throws FileNotFoundException
	 *             si le fichier est introuvable � l'emplacement sp�cifi�
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
				Operation o = processLine(scanner.nextLine(), labels, i); // appel
																			// de
																			// l'interpr�te
																			// �
																			// proprement
																			// parler
				if (o != null) { // ce n'est pas une ligne de commentaire
					list.add(o); // donc on ajoute l'operation au joueur
					i++; // et on est descendu d'une ligne r�elle
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
	 * M�me m�thode qu'au dessus mais in est le texte brut du warrior
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
			Operation o = processLine(scanner.nextLine(), labels, i);
			if (o != null) {
				list.add(o);
				i++;
			}
		}
		scanner.close();
		return list;
	}

	/**
	 * Le coeur de l'interpr�te.
	 * 
	 * @param line
	 *            la ligne � interpr�ter
	 * @param labels
	 *            les �tiquettes du fichier et leurs positions absolues
	 * @param i
	 *            la position absolue de la ligne lue
	 * @return une op�ration � ajouter au warrior
	 */
	private static Operation processLine(String line,
			HashMap<String, Integer> labels, int i) {
		if (line.startsWith(";")) { // c'est une ligne de commentaire
			return null;
		}

		else {
			Scanner scanner = new Scanner(line);
			String instructionPattern = "DAT|MOV|ADD|SUB|JMP|JMZ|JMN|CMP|SLT|DJN|SPL";
			String modePattern = "#|@|<";

			Operation o = new Operation();

//			while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//				scanner.next();
//			}

			try {
				if (!scanner.hasNext(Pattern.compile(instructionPattern))) {
					// il y a une �tiquette, on la saute, l'instruction viendra
					// apr�s
					scanner.next();

//					while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//						scanner.next();
//					}
				}

				// on r�cp�re l'instruction
				String ins = scanner.next();
				if (ins.equals("DAT")) {
					o.ins = Instruction.DAT;
				} else if (ins.equals("MOV")) {
					o.ins = Instruction.MOV;
				} else if (ins.equals("ADD")) {
					o.ins = Instruction.ADD;
				} else if (ins.equals("SUB")) {
					o.ins = Instruction.SUB;
				} else if (ins.equals("JMP")) {
					o.ins = Instruction.JMP;
				} else if (ins.equals("JMZ")) {
					o.ins = Instruction.JMZ;
				} else if (ins.equals("JMN")) {
					o.ins = Instruction.JMN;
				} else if (ins.equals("CMP")) {
					o.ins = Instruction.CMP;
				} else if (ins.equals("SLT")) {
					o.ins = Instruction.SLT;
				} else if (ins.equals("DJN")) {
					o.ins = Instruction.DJN;
				} else if (ins.equals("SPL")) {
					o.ins = Instruction.SPL;
				} else {
					System.out
							.println("Instruction invalide : \"" + ins + "\"");
				}
				
//				while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//					scanner.next();
//				}

				// -----------------------------
				// I) R�cup�ration de l'op�rande A

				// 1) r�cup�ration du mode de l'op�rande A
				String mode1 = scanner.findWithinHorizon(
						Pattern.compile(modePattern), 2);
				if (mode1 != null) {
					// si le mode est indiqu�, on cherche lequel est-ce
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
				
//				while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//					scanner.next();
//				}

				// 2) r�cup�ration de l'adresse de l'op�rande A
				// on veut r�cup�rer l'adresse toute seule m�me si elle est
				// coll�e � une ","
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

//				while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//					scanner.next();
//				}

				// ---------------------------------
				// II) R�cup�ration de l'op�rande B

				// s'il y a une ",", il y a un deuxi�me op�rande, sinon non
				if (",".equals(scanner.findWithinHorizon(",", 1))) {
					// la recherhce de la virgule place le scanner derri�re
					// elle...

					// 1) r�cup�ration du mode de l'op�rande B
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

//					while (" ".equals(scanner.findWithinHorizon(" ", 1))) { // on saute les espaces surnum�raires
//						scanner.next();
//					}

					// 2) r�cup�ration de l'adresse de l'op�rande B
					if (!scanner.hasNextInt()) {
						// c'est une �tiquette
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
				if (line.matches("[ \t\n\f\r]+")) {
					// il y a des lignes vides � la fin du fichier
				} else {
					System.err
							.println("cette ligne ne contient pas les �l�ments n�cessaires : "
									+ line);
				}
				return null;
			}
		}
	}
}