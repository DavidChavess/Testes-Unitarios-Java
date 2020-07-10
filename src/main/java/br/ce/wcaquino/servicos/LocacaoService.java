package br.ce.wcaquino.servicos;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocacaoException {
		if(filmes == null) {
			throw new LocacaoException("lista de filmes vazia");
		}
		
		if(usuario == null) {
			throw new LocacaoException("usuario vazio");
		}
		
		for(Filme f : filmes) {
			if(f.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}			
		}	
	
		Locacao locacao = new Locacao();
		locacao.getFilme().addAll(filmes);
		
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		double total = 0.0;
		
		for(int i = 0; i < filmes.size(); i++) {
			double valorFilme = filmes.get(i).getPrecoLocacao();
			
			switch(i) {
				case 2: valorFilme = valorFilme * 0.75;	break;
				case 3: valorFilme = valorFilme * 0.50;	break;
				case 4: valorFilme = valorFilme * 0.25;	break;
				case 5: valorFilme = valorFilme * 0.00;	break;
			}
			
			total += valorFilme;
		}	
				
		locacao.setValor(total);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}
}