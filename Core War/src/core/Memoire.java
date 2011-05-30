package core;

/**
La memoire est le tableau d'operation dans lequel les joueurs vont se déplacer.
La classe contient aussi les operations élementaires necessaire au jeu comme 
	l'évaluation des adresses a et b (evaluerA,et evaluerB)
	la definition et l'execution des différentes opérations possibles (executer).
 */
public class Memoire implements Cloneable {
	public Operation[] O;  // tableau d'operation représentant la mémoire

	public Memoire(Memoire mem) {
		this.O = mem.clone().O;
	}

	public Memoire clone() {
		Memoire clone = new Memoire("");
		Operation[] temp = new Operation[800];
		for (int i = 0; i < 800; i++) {
			temp[i] = new Operation();
			temp[i].ins = this.O[i].ins;
			temp[i].OA.a = this.O[i].OA.a;
			temp[i].OA.m = this.O[i].OA.m;
			temp[i].OB.a = this.O[i].OB.a;
			temp[i].OB.m = this.O[i].OB.m;
		}
		clone.O = temp;
		return clone;
	}

	public Memoire(String nom) {
		O = new Operation[800];
		for (int n = 0; n < 800; n++) {
			O[n] = new Operation();
		}
	}

	// Evaluation de l'adresse a
	public Adresse evaluerA(Adresse p) {
		Adresse a = new Adresse();
		Adresse un = new Adresse(1);
		if (O[p.get()].OA.m == "#") {
			a = new Adresse(0);
		} else {
			a = O[p.get()].OA.a;
			if (O[p.get()].OA.m != "_") {
				if (O[p.get()].OA.m == "<") {
					O[p.plus(a).get()].OB.a = O[p.plus(a).get()].OB.a.moins(un);
				}
				a = O[p.plus(a).get()].OB.a.plus(a);
			}
		}
		return a;
	}

	// Evaluation de l'adresse b
	public Adresse evaluerB(Adresse p) {
		Adresse b = new Adresse();
		Adresse un = new Adresse(1);
		if (O[p.get()].OB.m == "#") {
			b = new Adresse(0);
		} else {
			b = O[p.get()].OB.a;
			if (O[p.get()].OB.m != "_") {
				if (O[p.get()].OB.m == "<") {
					O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.moins(un);
				}
				b = O[p.plus(b).get()].OB.a.plus(b);
			}
		}
		return b;
	}

	// Execution de l'operation, spécifiée par son instruction et les adresses a et b.
	// Ces ici que sont définies les actions associées au différentes instructions.
	
	//L'adresse est passée en argument,
	//et la methode renvoie l'adresse du prochain processus à executer
	public Adresse executer(Adresse p) {
		Adresse un = new Adresse(1);
		Operation op = O[p.get()];
		Adresse a = evaluerA(p);
		Adresse b = evaluerB(p);

		if (op.ins == Instruction.DAT) {
			return p;
		} else if (op.ins == Instruction.MOV) {
			if (op.OA.m == "#") {
				O[p.plus(b).get()].OB.a = O[p.get()].OA.a;
			} else {
				O[p.plus(b).get()].ins = O[p.plus(a).get()].ins;
				O[p.plus(b).get()].OA.m = O[p.plus(a).get()].OA.m;
				O[p.plus(b).get()].OB.m = O[p.plus(a).get()].OB.m;
				O[p.plus(b).get()].OA.a = O[p.plus(a).get()].OA.a;
				O[p.plus(b).get()].OB.a = O[p.plus(a).get()].OB.a;
			}
			return p.plus(un);
		} else if (op.ins == Instruction.ADD) {
			if (op.OA.m == "#") {
				O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.plus(O[p
						.get()].OA.a);
			} else {
				O[p.plus(b).get()].OA.a = O[p.plus(b).get()].OA.a.plus(O[p
						.plus(a).get()].OA.a);
				O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.plus(O[p
						.plus(a).get()].OB.a);
			}
			return p.plus(un);
		} else if (op.ins == Instruction.SUB) {
			if (op.OA.m == "#") {
				O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.moins(O[p
						.get()].OA.a);
			} else {
				O[p.plus(b).get()].OA.a = O[p.plus(b).get()].OA.a.moins(O[p
						.plus(a).get()].OA.a);
				O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.moins(O[p
						.plus(a).get()].OB.a);
			}
			return p.plus(un);
		} else if (op.ins == Instruction.JMP) {
			return p.plus(a);
		} else if (op.ins == Instruction.JMZ) {
			if (O[p.plus(b).get()].OB.a.get() == 0) {
				return p.plus(a);
			} else {
				return p.plus(un);
			}
		} else if (op.ins == Instruction.JMN) {
			if (O[p.plus(b).get()].OB.a.get() != 0) {
				return p.plus(a);
			} else {
				return p.plus(un);
			}
		} else if (op.ins == Instruction.CMP) {
			boolean t;
			if (op.OA.m == "#") {
				t = (O[p.get()].OA.a.equals(O[p.plus(b).get()].OB.a));
			} else {
				t = (O[p.plus(a).get()].equals(O[p.plus(b).get()])
						&& O[p.plus(a).get()].OA.m
								.equals(O[p.plus(b).get()].OA.m)
						&& O[p.plus(a).get()].OB.m
								.equals(O[p.plus(b).get()].OB.m)
						&& O[p.plus(a).get()].OA.a
								.equals(O[p.plus(b).get()].OA.a) && O[p.plus(a)
						.get()].OB.a.equals(O[p.plus(b).get()].OB.a));
			}
			if (t) {
				return p.plus(un.plus(un));
			} else {
				return p.plus(un);
			}
		} else if (op.ins == Instruction.SLT) {
			boolean t;
			if (op.OA.m == "#") {
				t = (O[p.get()].OA.a.get() < O[p.plus(b).get()].OB.a.get());
			} else
				t = (O[p.plus(a).get()].OB.a.get() < O[p.plus(b).get()].OB.a
						.get());
			if (t) {
				return p.plus(un.plus(un));
			} else {
				return p.plus(un);
			}
		} else if (op.ins == Instruction.DJN) {
			Adresse v = new Adresse();
			if (op.OB.m == "#") {
				v = O[p.get()].OB.a;
			} else {
				O[p.plus(b).get()].OB.a = O[p.plus(b).get()].OB.a.moins(un);
				v = O[p.plus(b).get()].OB.a;
			}
			if (v.get() != 0) {
				return p.plus(a);
			} else {
				return p.plus(un);
			}
		} else if (op.ins == Instruction.SPL) {
			return p.plus(a);
		} else {
			return null;
		}
	}

}
