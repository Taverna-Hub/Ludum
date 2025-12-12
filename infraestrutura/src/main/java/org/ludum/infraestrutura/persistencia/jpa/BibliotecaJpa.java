package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BIBLIOTECA")
class BibliotecaJpa {
    @Id
    String contaId;

    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<ItemBibliotecaJpa> itens = new ArrayList<>();
}

@Entity
@Table(name = "ITEM_BIBLIOTECA")
class ItemBibliotecaJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "biblioteca_id")
    BibliotecaJpa biblioteca;

    String jogoId;

    @Enumerated(EnumType.STRING)
    ModeloDeAcesso modeloDeAcesso;

    LocalDateTime dataAdicao;

    boolean baixado;
}

interface BibliotecaJpaRepository extends JpaRepository<BibliotecaJpa, String> {
}

@Repository
class BibliotecaRepositoryImpl implements BibliotecaRepository {

    @Autowired
    private BibliotecaJpaRepository repository;

    @Autowired
    private JpaMapeador mapeador;

    @Override
    public Biblioteca obterPorJogador(ContaId contaId) {
        return repository.findById(contaId.getValue())
                .map(jpa -> mapeador.map(jpa, Biblioteca.class))
                .orElse(null);
    }

    @Override
    public void salvar(Biblioteca biblioteca) {
        BibliotecaJpa jpa = mapeador.map(biblioteca, BibliotecaJpa.class);
        repository.save(jpa);
    }
}
