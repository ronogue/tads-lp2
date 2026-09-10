package br.edu.ifsp.biblioteca.service;

import br.edu.ifsp.biblioteca.domain.Usuario;
import br.edu.ifsp.biblioteca.exception.RegraDeNegocioException;
import br.edu.ifsp.biblioteca.repository.ILivroRepository;
import br.edu.ifsp.biblioteca.repository.IUsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    public Usuario cadastrar(Usuario usuario) {

        if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
            throw new RegraDeNegocioException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new RegraDeNegocioException("E-mail é obrigatório");
        }

        this.validarEmail(usuario.getEmail());

        return this.usuarioRepository.salvar(usuario);
    }

    public Usuario buscarPorId(Long id) {

        Optional<Usuario> usuarioOptional = this.usuarioRepository.buscarPorId(id);

        if (usuarioOptional.isPresent()) {
            return usuarioOptional.get();
        }

        throw new RegraDeNegocioException("Usuário não encontrado: " + id);
    }

    private void validarEmail(String email) {
        Optional<Usuario> usuarioOptional = this.usuarioRepository.buscarPorEmail(email);

        if (usuarioOptional.isPresent()) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário cadastrado com o e-mail: " + email
            );
        }
    }

    public List<Usuario> listarTodos() {
        return this.usuarioRepository.listarTodos();
    }
}
