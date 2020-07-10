package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.DivisaoPorZeroException;

public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
	}

	@Test
	public void testeCalculadora_soma() {
		int a = 5;
		int b = 3;
	
		int res = calc.somar(a,b);
		
		assertEquals(8, res);
	}
	
	@Test
	public void testeCalculadora_subtrai() {
		int a = 5;
		int b = 3;
	
		int res = calc.subtrai(a, b);
		
		assertEquals(2, res);
	}
	
	@Test
	public void testeCalculadora_divide() throws DivisaoPorZeroException {
		int a = 6;
		int b = 2;
	
		int res = calc.divide(a, b);
		
		assertEquals(3, res);
	}
	
	@Test( expected = DivisaoPorZeroException.class)
	public void testeCalculadora_divisaoPorZero() throws DivisaoPorZeroException {
		int a = 6;
		int b = 0;
	
		int res = calc.divide(a, b);
		
		assertEquals(3, res);
	}
}
