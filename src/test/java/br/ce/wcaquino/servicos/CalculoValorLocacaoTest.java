package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService service; 
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value = 1)
	public Double valorLocacao;
	
	@Parameter(value = 2)
	public String cenario;
	
	private static Filme filme1 = new Filme("filme 1", 1, 4.0);
	private static Filme filme2 = new Filme("filme 2", 1, 4.0);
	private static Filme filme3 = new Filme("filme 3", 1, 4.0);
	private static Filme filme4 = new Filme("filme 4", 1, 4.0);
	private static Filme filme5 = new Filme("filme 5", 1, 4.0);
	private static Filme filme6 = new Filme("filme 6", 1, 4.0);
	private static Filme filme7 = new Filme("filme 6", 1, 4.0);
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	
	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {			
			{Arrays.asList(filme1, filme2), 8.0, "Sem desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0,  "5 filmes; 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 filmes; 100%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "Sem desconto"},
		});
	}
		
	@Test
	public void calcularValorLocacaoComDesconto() throws FilmeSemEstoqueException, LocacaoException {
		
		Usuario usuario = new Usuario("Usuario 1");
	
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertThat(locacao.getValor(),is(valorLocacao));
		
		
	}
}
