package builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
	private Usuario usuario;
	
	public UsuarioBuilder() {}
	
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario();
		builder.usuario.setNome("usuario 1");
		return builder;
	}
	public Usuario agora() {
		return usuario;
	}
}
