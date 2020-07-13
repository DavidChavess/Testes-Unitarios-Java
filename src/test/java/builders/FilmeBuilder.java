package builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {
	private Filme filme;
	
	public FilmeBuilder() {}
	
	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("filme 1");
		builder.filme.setEstoque(1);
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public static FilmeBuilder umFilmeSemEstoque() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("filme 1");
		builder.filme.setEstoque(0);
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public FilmeBuilder comValor(Double valor) {
		this.filme.setPrecoLocacao(valor);
		return this;
	}
	
	public FilmeBuilder semEstoque() {
		this.filme.setEstoque(0);
		return this;
	}
	
	public Filme agora() {
		return filme;
	}
}
