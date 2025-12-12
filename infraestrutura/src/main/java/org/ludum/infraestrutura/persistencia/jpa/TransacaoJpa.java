package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "TRANSACAO")
public class TransacaoJpa {
    @Id
    String id;

    @Column(name = "CONTA_ORIGEM_ID")
    String contaOrigemId;

    @Column(name = "CONTA_DESTINO_ID")
    String contaDestinoId;

    TipoTransacao tipo;

    StatusTransacao status;

    LocalDateTime data;

    BigDecimal valor;
}

interface TransacaoJpaRepository extends JpaRepository<TransacaoJpa, String> {
    List<TransacaoJpa> findByContaOrigemIdOrContaDestinoIdOrderByDataDesc(String contaOrigemId, String contaDestinoId);
}

@Repository
class TransacaoRepositoryImpl implements TransacaoRepository {
    @Autowired
    TransacaoJpaRepository repositorio;

    @Autowired
    ReciboJpaRepository reciboJpaRepository;

    @Override
    public void salvar(Transacao transacao) {
        TransacaoJpa transacaoJpa = new TransacaoJpa();
        transacaoJpa.id = transacao.getTransacaoId().getValue();
        transacaoJpa.contaOrigemId = transacao.getContaOrigem() != null ? transacao.getContaOrigem().getValue() : null;
        transacaoJpa.contaDestinoId = transacao.getContaDestino() != null ? transacao.getContaDestino().getValue() : null;
        transacaoJpa.tipo = transacao.getTipo();
        transacaoJpa.status = transacao.getStatus();
        transacaoJpa.data = transacao.getData();
        transacaoJpa.valor = transacao.getValor();

        repositorio.save(transacaoJpa);
    }

    @Override
    public Transacao obterPorId(TransacaoId id) {
        return repositorio.findById(id.getValue()).map(this::mapToTransacao).orElse(null);
    }

    @Override
    public List<Transacao> obterPorContaId(org.ludum.dominio.identidade.conta.entities.ContaId contaId) {
        String contaIdValue = contaId.getValue();
        return repositorio.findByContaOrigemIdOrContaDestinoIdOrderByDataDesc(contaIdValue, contaIdValue)
                .stream()
                .map(this::mapToTransacao)
                .collect(Collectors.toList());
    }

    private Transacao mapToTransacao(TransacaoJpa transacaoJpa) {
        return new Transacao(
                new TransacaoId(transacaoJpa.id),
                transacaoJpa.contaOrigemId != null ? new org.ludum.dominio.identidade.conta.entities.ContaId(transacaoJpa.contaOrigemId) : null,
                transacaoJpa.contaDestinoId != null ? new org.ludum.dominio.identidade.conta.entities.ContaId(transacaoJpa.contaDestinoId) : null,
                transacaoJpa.tipo,
                transacaoJpa.status,
                transacaoJpa.data,
                transacaoJpa.valor);
    }

    @Override
    public void salvarRecibo(Recibo recibo) {
        ReciboJpa reciboJpa = new ReciboJpa();
        reciboJpa.id = recibo.getId().getValue();
        reciboJpa.data = recibo.getData();
        reciboJpa.valor = recibo.getValor();

        reciboJpaRepository.save(reciboJpa);
    }
}