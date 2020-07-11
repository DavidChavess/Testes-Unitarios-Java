package teste.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ehUmNumero extends TypeSafeMatcher<Number> {
	
	public Number valorEsperado;
	
	public ehUmNumero(Number valorEsperado) {
		this.valorEsperado = valorEsperado;
	}
	

	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean matchesSafely(Number valorAtual) {
		return valorAtual.equals(valorEsperado);
	}

}
