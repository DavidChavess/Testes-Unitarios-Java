package teste.matchers;

import static org.junit.Assert.assertThat;
import static teste.matchers.NumerosPropriosMatchers.issoEh20;
import static teste.matchers.NumerosPropriosMatchers.issoEh30;
import static teste.matchers.NumerosPropriosMatchers.issoEhDez;
import static teste.matchers.NumerosPropriosMatchers.issoEhIgualA;

import org.junit.Test;

public class NumerosTest {

	@Test 
	public void testeDeNumero() {
	    assertThat(10, issoEhDez());
	    assertThat(20, issoEh20());
	    assertThat(30, issoEh30());
	    assertThat(40, issoEhIgualA(40));
	}
}
