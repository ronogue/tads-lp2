package br.edu.ifsp.biblioteca.service;

import br.edu.ifsp.biblioteca.domain.Usuario;
import br.edu.ifsp.biblioteca.dto.NovoUsuario;
import br.edu.ifsp.biblioteca.dto.UsuarioDetalhe;
import br.edu.ifsp.biblioteca.exception.RegraDeNegocioException;
import br.edu.ifsp.biblioteca.repository.IUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    @Transactional
    public UsuarioDetalhe cadastrar(NovoUsuario novoUsuario) {

        Usuario usuario = novoUsuario.toEntity();

        if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
            throw new RegraDeNegocioException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new RegraDeNegocioException("E-mail é obrigatório");
        }

        this.validarEmail(usuario.getEmail());

        return UsuarioDetalhe.of(this.usuarioRepository.save(usuario));
    }

    public UsuarioDetalhe buscarPorId(Long id) {

        Optional<Usuario> usuarioOptional = this.usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            return UsuarioDetalhe.of(usuarioOptional.get());
        }

        throw new RegraDeNegocioException("Usuário não encontrado: " + id);
    }

    private void validarEmail(String email) {
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByEmailIgnoreCase(email);

        if (usuarioOptional.isPresent()) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário cadastrado com o e-mail: " + email
            );
        }
    }

    public List<UsuarioDetalhe> listarTodos() {
        return this.usuarioRepository.findAll()
                .stream()
                .map(UsuarioDetalhe::of)
                .toList();
    }
}
