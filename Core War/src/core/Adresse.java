package core;

/**
La classe "Adresse" représente un entier
dont on ne peut obtenir que sa valeur modulo 800 (la taille de la memoire)
par la fonction get().
 */

public class Adresse {
	private int i;

	public Adresse() {
		this.i = 0;
	}

	public Adresse(int n) {
		this.i = mod(n);
	}

	public boolean equals(Adresse ad) {
		return (this.get() == ad.get());
	}

	public int get() {
		return mod(i);
	}

	public int mod(int n) {
		int k = n % 800;
		if (k < 0)
			k = k + 800;
		return (k);
	}

	public Adresse moins(Adresse a) {
		Adresse b = new Adresse(mod(this.i - a.i));
		return (b);
	}

	public Adresse plus(Adresse a) {
		Adresse b = new Adresse(mod(i + a.i));
		return (b);
	}

	public int plus(int k) {
		return (mod(i + k));
	}

	@Override
	public String toString() {
		return ("" + get());
	}
}
