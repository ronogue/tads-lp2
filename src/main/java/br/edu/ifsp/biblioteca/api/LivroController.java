package br.edu.ifsp.biblioteca.api;

import br.edu.ifsp.biblioteca.dto.LivroDetalhe;
import br.edu.ifsp.biblioteca.dto.NovoLivro;
import br.edu.ifsp.biblioteca.service.LivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    /**
     * GET /api/livros
     * GET /api/livros?titulo=casmurro
     * GET /api/livros?autor=machado
     * GET /api/livros?disponivel=true
     * @return
     */
    @GetMapping
    public List<LivroDetalhe> listar(
        @RequestParam(required = false) String titulo,
        @RequestParam(required = false) String autor,
        @RequestParam(required = false) Boolean disponivel
    ) {

        if (titulo != null && !titulo.isBlank()) {
            return this.livroService.buscarPorTitulo(titulo);
        }

        // Tarefa de Casa!!!
        if (autor != null && !titulo.isBlank()) {
            // return this.livroService...
        }

        // Tarefa de Casa !!!
        if (Boolean.TRUE.equals(disponivel)) {
//            return this.livroService...
        }

        return livroService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<LivroDetalhe> cadastrar(
            @RequestBody NovoLivro novoLivro
    ) {
        LivroDetalhe l = this.livroService.cadastrar(novoLivro);

        return ResponseEntity
                .created(URI.create("/api/livros/" + l.id()))
                .body(l);
    }

    @GetMapping("/{id}")
    public LivroDetalhe buscar(@PathVariable Long id) {
        return this.livroService.buscarPorId(id);
    }
}
