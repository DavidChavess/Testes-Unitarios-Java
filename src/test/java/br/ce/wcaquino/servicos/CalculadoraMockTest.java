package br.ce.wcaquino.servicos;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	@Mock
	private Calculadora calc;
	
	@Spy
	private Calculadora spy;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void teste() {

		Mockito.when(calc.somar(1, 5)).thenReturn(8);
		//Mockito.when(spy.somar(1, 5)).thenReturn(8);
		Mockito.doReturn(8).when(spy).somar(1, 5);
		Mockito.doNothing().when(spy).imprimi();
		
		System.out.println("Mock: " + calc.somar(1, 5));
		System.out.println("Spy: " + spy.somar(1, 5));
		
		System.out.println("Mock:");
		calc.imprimi();

		System.out.println("Spy:");
		spy.imprimi();
		
	}
}
