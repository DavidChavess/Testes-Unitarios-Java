package br.ce.wcaquino.servicos;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
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
	
	@Test
	public void teste() throws FilmeSemEstoqueException, LocacaoException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.05);
		LocacaoService service = new LocacaoService();
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		//validacao
		error.checkThat(locacao.getValor(), is(equalTo(5.05)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true) );
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacao_FilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.05);
		LocacaoService service = new LocacaoService();
		
		//acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void testeLocacao_UsuarioVazio() throws FilmeSemEstoqueException{
		//cenario
		LocacaoService service = new LocacaoService();
		Filme filme = new Filme("Filme 1", 1, 5.05);
		
		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		}catch (LocacaoException e) {
			assertThat(e.getMessage(), is("usuario vazio"));
		}
	}
	
	@Test
	public void testeLocacao_FilmeVazio() throws FilmeSemEstoqueException {
		LocacaoService service = new LocacaoService();		
		Usuario usuario = new Usuario("Usuario 1");
		
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocacaoException e) {
			assertThat(e.getMessage(), is("filme vazio"));
		}
	}
}