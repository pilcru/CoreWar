package core;

/**
Une operation est ce qui remplit la mémoire.
Chaque opération comporte : 
	une instruction définissant l'action à executer
	un opérande OA
	un opérande OB
 */
public class Operation {
	public Instruction ins;
	public Operande OA;
	public Operande OB;

	public Operation() {
		ins = Instruction.DAT;
		OA = new Operande();
		OB = new Operande();
	}

	@Override
	public String toString() {
		String s = ins.toString() + OA.m + OA.a.get() + OB.m + OB.a.get();
		return s;
	}

	public boolean equals(Operation o) {
		return (ins.equals(o.ins) && OA.equals(o.OA) && OB.equals(o.OB));
	}

}
