package br.ce.wcaquino.servicos;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector(); 
	
	private LocacaoService service; 
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	
	@Test
	public void teste() throws Exception{
		Usuario usuario = new Usuario("Usuario 1");
		
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 4.0),
			new Filme("Filme 2", 1, 5.0)
		);
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		error.checkThat(locacao.getValor(), is(equalTo(9.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true) );
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 5.05),
			new Filme("Filme 2", 0, 5.05),
			new Filme("Filme 3", 1, 5.05)
		);
		
		
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{	
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 5.05),
			new Filme("Filme 2", 1, 5.05),
			new Filme("Filme 3", 1, 5.05)
		);
		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		}catch (LocacaoException e) {
			assertThat(e.getMessage(), is("usuario vazio"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException {
		Usuario usuario = new Usuario("Usuario 1");
		
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocacaoException e) {
			assertThat(e.getMessage(), is("lista de filmes vazia"));
		}
	}
	
	@Test
	public void devePagar75pctNoFilme3() throws FilmeSemEstoqueException, LocacaoException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 4.0),
			new Filme("Filme 2", 1, 4.0),
			new Filme("Filme 3", 1, 4.0)
		);
				
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertThat(locacao.getValor(),is(11.0));
	}
	
	@Test
	public void devePagar50pctNoFilme4() throws FilmeSemEstoqueException, LocacaoException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 4.0),
			new Filme("Filme 2", 1, 4.0),
			new Filme("Filme 3", 1, 4.0),
			new Filme("Filme 4", 1, 4.0)
		);
				
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertThat(locacao.getValor(),is(13.0));
	}
	
	@Test
	public void devePagar25pctNoFilme5() throws FilmeSemEstoqueException, LocacaoException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 4.0),
			new Filme("Filme 2", 1, 4.0),
			new Filme("Filme 3", 1, 4.0),
			new Filme("Filme 4", 1, 4.0),
			new Filme("Filme 5", 1, 4.0)
			
		);
				
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertThat(locacao.getValor(),is(14.0));
	}
	
	
	@Test
	public void devePagar0pctNoFilme6() throws FilmeSemEstoqueException, LocacaoException {
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 1, 4.0),
			new Filme("Filme 2", 1, 4.0),
			new Filme("Filme 3", 1, 4.0),
			new Filme("Filme 4", 1, 4.0),
			new Filme("Filme 5", 1, 4.0),
			new Filme("Filme 6", 1, 4.0)
			
		);
				
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		assertThat(locacao.getValor(),is(14.0));
	}
}