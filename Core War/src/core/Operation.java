package core;

/**
Une operation est ce qui remplit la m�moire.
Chaque op�ration comporte : 
	une instruction d�finissant l'action � executer
	un op�rande OA
	un op�rande OB
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
