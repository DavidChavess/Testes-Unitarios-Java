package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.DivisaoPorZeroException;

public class Calculadora {
	
	public int somar(int a, int b) {
		return a + b;
	}

	public int subtrai(int a, int b) {
		return a - b;
	}
	
	public double subtrai(double a, double b) {
		return a - b;
	}
	
	public int divide(int a, int b) throws DivisaoPorZeroException {
		if(b == 0) {
			throw new DivisaoPorZeroException();
		}
		return a / b;
	}
	
	public double valorComDesconto(int qtd, double valor) {
		if(qtd == 3) {
			return subtrai(valor, (valor * 0.25));
		}if(qtd == 4) {
			return subtrai(valor, (valor * 0.50));
		}if(qtd == 5) {
			return subtrai(valor, (valor * 0.75));
		}if(qtd >= 6) {
			return subtrai(valor, valor);
		}
		return valor;
	}

}
