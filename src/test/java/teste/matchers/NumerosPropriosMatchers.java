package teste.matchers;

public class NumerosPropriosMatchers {
	public static ehUmNumero issoEhDez() {
		return new ehUmNumero(10);
	}
	
	public static ehUmNumero issoEh20(){
		return new ehUmNumero(20);
	}
	
	public static ehUmNumero issoEh30() {
		return new ehUmNumero(30);
	}

	public static ehUmNumero issoEhIgualA(Number numero) {
		return new ehUmNumero(numero);
	}
}
