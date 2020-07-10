package br.ce.wcaquino.servicos;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

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
		Filme filme = new Filme("Filme 1", 1, 5.05);
		
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		error.checkThat(locacao.getValor(), is(equalTo(5.05)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true) );
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacao_FilmeSemEstoque() throws Exception {
		Usuario usuario = new Usuario("Usuario 1");
		
		Filme filme = new Filme("Filme 1", 0, 5.05);
		
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void testeLocacao_UsuarioVazio() throws FilmeSemEstoqueException{	
		Filme filme = new Filme("Filme 1", 1, 5.05);
		
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		}catch (LocacaoException e) {
			assertThat(e.getMessage(), is("usuario vazio"));
		}
	}
	
	@Test
	public void testeLocacao_FilmeVazio() throws FilmeSemEstoqueException {
		Usuario usuario = new Usuario("Usuario 1");
		
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocacaoException e) {
			assertThat(e.getMessage(), is("filme vazio"));
		}
	}
}