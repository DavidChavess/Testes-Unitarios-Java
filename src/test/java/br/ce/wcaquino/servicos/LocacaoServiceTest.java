package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static builders.FilmeBuilder.umFilme;
import static builders.FilmeBuilder.umFilmeSemEstoque;
import static builders.LocacaoBuilder.umLocacao;
import static builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;

import com.sun.tools.javac.comp.Analyzer;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.exceptions.NumeroMenorException;
import br.ce.wcaquino.exceptions.UsuarioNegativadoException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import dao.LocacaoDao;

public class LocacaoServiceTest {
	
	@Mock
	private LocacaoDao dao;	
	
	@Mock
	private SPCService spc;	
	
	@Mock
	private EmailService email;
	
	@InjectMocks
	private LocacaoService service; 
	
	@Rule
	public ErrorCollector error = new ErrorCollector(); 
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
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
	public void naoDeveAlugarFilmeUsuarioNegativado() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//when(spc.usuarioNegativado(usuario)).thenReturn(true);
		when(spc.usuarioNegativado(any(Usuario.class))).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		}catch (UsuarioNegativadoException e) {
			Assert.assertThat(e.getMessage(), is("Usuario Negativado"));
		}
		
		Mockito.verify(spc).usuarioNegativado(usuario);
	}
	
	@Test 
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		
		Usuario usuario = umUsuario().comNome("usuario em dia").agora();
		Usuario usuario2 = umUsuario().comNome("atrasado 1").agora();
		Usuario usuario3 = umUsuario().comNome("atrasado 2").agora();
		Usuario usuario4 = umUsuario().comNome("atrasado 3").agora();
		
		List<Locacao> locacoes = Arrays.asList(
			umLocacao().comUsuario(usuario).agora(),
			umLocacao().atrasado().comUsuario(usuario2).agora(),
			umLocacao().atrasado().comUsuario(usuario3).agora(),
			umLocacao().atrasado().comUsuario(usuario4).agora()
		);
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		verify(email, times(3)).notificarAtraso(any(Usuario.class));
		verify(email, never()).notificarAtraso(usuario);
		verify(email).notificarAtraso(usuario2);
		verify(email).notificarAtraso(usuario3);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		when(spc.usuarioNegativado(usuario)).thenThrow(new Exception("falha catastr�fica"));
		
		try {
			//acao
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		}catch (LocacaoException e) {
			//verificacao
			assertThat(e.getMessage(), is("Problemas com SPC!, Tente novamente"));
		}
	}
	
	@Test
	public void deveProrrogarLocacao() {
		Locacao locacao = umLocacao().agora();
		
		service.prorrogarLocacao(locacao, 3);
		
		
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDeDias(3));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
	}
	
	@Test
	public void deveRetornarExcecaoSeUmNumeroForMenor() {
		//cenario
		int x = 50;
		try {
			//Acao
			service.testeDeNumero(x);
			Assert.fail();
		}catch (NumeroMenorException e) {
			//verificacao
			assertThat(e.getMessage(), is("o numero � menor"));
		}
	}
}