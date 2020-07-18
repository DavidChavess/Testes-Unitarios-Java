package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static builders.FilmeBuilder.umFilme;
import static builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import dao.LocacaoDao;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest_PowerMock {
	
	@InjectMocks
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
		service = PowerMockito.spy(service);
	}
	
	@Test
	public void deveAlugarFilmes() throws Exception{
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(9.0).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(14,7,2020));
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		error.checkThat(locacao.getValor(), is(equalTo(9.0)));
	  	error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
	    error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));  
	}
	
	@Test
	public void deveEntregarFilmeNaSegundaSeAlugarNoSabado() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		Usuario usuario = umUsuario().agora();

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(18,7,2020));
	
		Locacao locacao = service.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getDataRetorno(), caiNumaSegunda());
	}	
		
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		Usuario usuario = umUsuario().agora();
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		Locacao locacao = service.alugarFilme(usuario, filmes);
				
		error.checkThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);	
	}
	
	@Test
	public void deveCalcularValorDoFilme() throws Exception {
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		error.checkThat(valor, is(4.0));	
	}
}