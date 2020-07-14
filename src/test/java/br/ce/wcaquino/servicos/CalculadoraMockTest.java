package br.ce.wcaquino.servicos;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculadoraMockTest {
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(8);

		System.out.println(calc.somar(1, 5));
		System.out.println(argCapt.getAllValues());
		
		
	}
}
