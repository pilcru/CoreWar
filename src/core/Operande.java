package core;

/**
La classe operande, représente la partie de l'operation O[p] regroupant
	l'adresse A[p] (a)
	son mode d'execution (m). 
 */
public class Operande {
	public String m;	// mode d'éxecution de a
	public Adresse a;	// adresse de l'opérande

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