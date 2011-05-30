package core;

/**
La classe operande, repr�sente la partie de l'operation O[p] regroupant
	l'adresse A[p] (a)
	son mode d'execution (m). 
 */
public class Operande {
	public String m;	// mode d'�xecution de a
	public Adresse a;	// adresse de l'op�rande

	public Operande() {
		m = "#";
		a = new Adresse();
	}

	public boolean equals(Operande o) {
		return (m.equals(o.m) && a.equals(o.a));
	}
}
// # immediat
// @ indirect
// _ direct
// < indirect avec decrement