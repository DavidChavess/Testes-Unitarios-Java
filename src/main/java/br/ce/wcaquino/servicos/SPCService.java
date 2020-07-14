package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public interface SPCService {
	boolean usuarioNegativado(Usuario usuario) throws Exception;
}
