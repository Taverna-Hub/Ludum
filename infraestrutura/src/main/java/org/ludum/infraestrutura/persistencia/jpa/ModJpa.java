package org.ludum.infraestrutura.persistencia.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.oficina.mod.entidades.Mod;
import org.ludum.dominio.oficina.mod.repositorios.ModRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;


@Entity
@Table(name = "mods")
public class ModJpa {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @Column(name = "jogo_id", nullable = false, length = 36)
    String jogoId;

    @Column(name = "autor_id", nullable = false, length = 36)
    String autorId;

    @Column(name = "nome", nullable = false, length = 255)
    String nome;

    @Column(name = "descricao", nullable = false, length = 2000)
    String descricao;

    @Column(name = "status", nullable = false)
    String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "mods_versoes", 
        joinColumns = @JoinColumn(name = "mod_id", referencedColumnName = "id")) List<VersaoModJpa> versoes = new ArrayList<>();

    public ModJpa() {}

    public ModJpa(String id, String jogoId, String autorId, String nome, String descricao, String status, List<VersaoModJpa> versoes) {
        this.id = id;
        this.jogoId = jogoId;
        this.autorId = autorId;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.versoes = versoes == null ? new ArrayList<>() : versoes;
    }

    public String getId() { return id; }
    public String getJogoId() { return jogoId; }
    public String getAutorId() { return autorId; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public List<VersaoModJpa> getVersoes() { return versoes; }
}

interface ModJpaRepository extends JpaRepository<ModJpa, String> {
    List<ModJpa> findByJogoId(String jogoId);
    List<ModJpa> findByAutorId(String autorId);
}

@Repository
class ModRepositoryImpl implements ModRepository {

    @Autowired
    private ModJpaRepository repositorio;

    @Autowired
    private JpaMapeador mapeador;

    @Override
    public void salvar(Mod mod) {
        repositorio.save(mapeador.map(mod, ModJpa.class));
    }

    @Override
    public Optional<Mod> buscarPorId(String id) {
        return repositorio.findById(id)
                .map(jpa -> mapeador.map(jpa, Mod.class));
    }

    @Override
    public List<Mod> listarPorJogo(JogoId jogoId) {
        return repositorio.findByJogoId(jogoId.getValue()).stream()
                .map(jpa -> mapeador.map(jpa, Mod.class))
                .toList();
    }
    
    @Override
    public List<Mod> listarPorAutor(ContaId autorId) {
        return repositorio.findByAutorId(autorId.getValue()).stream()
                .map(jpa -> mapeador.map(jpa, Mod.class))
                .toList();
    }

    @Override
    public void remover(Mod mod) {
        repositorio.deleteById(mod.getId());
    }
}