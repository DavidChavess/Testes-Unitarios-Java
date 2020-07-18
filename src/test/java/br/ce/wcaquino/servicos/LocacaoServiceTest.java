package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static builders.FilmeBuilder.umFilme;
import static builders.FilmeBuilder.umFilmeSemEstoque;
import static builders.LocacaoBuilder.umLocacao;
import static builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.exceptions.UsuarioNegativadoException;
import builders.FilmeBuilder;
import builders.UsuarioBuilder;
import dao.LocacaoDao;

public class LocacaoServiceTest {
	
	@InjectMocks @Spy
	private LocacaoService service; 
	
	@Mock
	private LocacaoDao dao;	
	
	@Mock
	private SPCService spc;	
	
	@Mock
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector(); 
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveAlugarFilmes() throws Exception{
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(9.0).agora());
		
		Mockito.doReturn(obterData(20, 7, 2020)).when(service).obterData();
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		error.checkThat(locacao.getValor(), is(equalTo(9.0)));
	    error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(20, 7, 2020)), is(true));
	    error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(21, 7, 2020)), is(true));
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
			error.checkThat(e.getMessage(), is("usuario vazio"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, UsuarioNegativadoException {
		Usuario usuario = umUsuario().agora();
		
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocacaoException e) {
			error.checkThat(e.getMessage(), is("lista de filmes vazia"));
		}
	}
	
	@Test
	public void deveEntregarFilmeNaSegundaSeAlugarNoSabado() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		Usuario usuario = umUsuario().agora();
		
		Mockito.doReturn(obterData(29, 04, 2017)).when(service).obterData();
		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getDataRetorno(), caiNumaSegunda());
	}	
	
	@Test
	public void naoDeveAlugarFilmeUsuarioNegativado() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
	
		when(spc.usuarioNegativado(any(Usuario.class))).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		}catch (UsuarioNegativadoException e) {
			error.checkThat(e.getMessage(), is("Usuario Negativado"));
		}
		
		Mockito.verify(spc).usuarioNegativado(usuario);
	}
	
	@Test 
	public void deveEnviarEmailParaLocacoesAtrasadas() {		
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
		
		service.notificarAtrasos();
		
		verify(email, times(3)).notificarAtraso(any(Usuario.class));
		verify(email, never()).notificarAtraso(usuario);
		verify(email).notificarAtraso(usuario2);
		verify(email).notificarAtraso(usuario3);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		when(spc.usuarioNegativado(usuario)).thenThrow(new Exception("falha catastrófica"));
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		}catch (LocacaoException e) {
			error.checkThat(e.getMessage(), is("Problemas com SPC!, Tente novamente"));
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
	public void deveCalcularValorDoFilme() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		/*----USAR ESTA FORMA QUANDO METODO FOR PRIVADO -------*/
		Class<LocacaoService> clazz = LocacaoService.class;
		java.lang.reflect.Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);		
		
		error.checkThat(valor, is(4.0));	
	}
	
}