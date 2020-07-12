package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static builders.FilmeBuilder.umFilme;
import static builders.FilmeBuilder.umFilmeSemEstoque;
import static builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.Mockito;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.exceptions.UsuarioNegativadoException;
import br.ce.wcaquino.utils.DataUtils;
import dao.LocacaoDao;

public class LocacaoServiceTest {
	
	private LocacaoDao dao;
	
	private SPCService consultaSpc;
	
	private LocacaoService service; 
	
	@Rule
	public ErrorCollector error = new ErrorCollector(); 
	
	@Before
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDao.class);
		consultaSpc = Mockito.mock(SPCService.class);
		service.setLocacaoDao(dao);
		service.setConsultaSpc(consultaSpc);
	}
	
	@Test
	public void deveAlugarFilmes() throws Exception{
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(9.0).agora());
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		error.checkThat(locacao.getValor(), is(equalTo(9.0)));
	    error.checkThat(locacao.getDataLocacao(), ehHoje());
	    error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDeDias(1));
	  
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
				
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException, UsuarioNegativadoException{	
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		}catch (LocacaoException e) {
			assertThat(e.getMessage(), is("usuario vazio"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, UsuarioNegativadoException {
		Usuario usuario = umUsuario().agora();
		
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocacaoException e) {
			assertThat(e.getMessage(), is("lista de filmes vazia"));
		}
	}
	
	@Test
	public void deveEntregarFilmeNaSegundaSeAlugarNoSabado() throws Exception {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		Usuario usuario = umUsuario().agora();

		Locacao locacao = service.alugarFilme(usuario, filmes);

		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}	
	
	@Test
	public void naoDeveAlugarFilmeUsuarioNegativado() throws FilmeSemEstoqueException, LocacaoException {
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("usuario2").agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(consultaSpc.usuarioNegativado(usuario)).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		}catch (UsuarioNegativadoException e) {
			Assert.assertThat(e.getMessage(), is("Usuario Negativado"));
		}
	}
}