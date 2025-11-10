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

@Entity
@Table(name = "TRANSACAO")
public class TransacaoJpa {
    @Id
    String id;

    // @ManyToOne
    // @JoinColumn(name = "CONTA_ORIGEM_ID")
    // ContaJpa contaOrigem;

    // @ManyToOne
    // @JoinColumn(name = "CONTA_DESTINO_ID")
    // ContaJpa contaOrigem;

    TipoTransacao tipo;

    StatusTransacao status;

    LocalDateTime data;

    BigDecimal valor;
}

interface TransacaoJpaRepository extends JpaRepository<TransacaoJpa, String> {

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
        transacaoJpa.tipo = transacao.getTipo();
        transacaoJpa.status = transacao.getStatus();
        transacaoJpa.data = transacao.getData();
        transacaoJpa.valor = transacao.getValor();

        repositorio.save(transacaoJpa);
    }

    @Override
    public Transacao obterPorId(TransacaoId id) {
        return repositorio.findById(id.getValue()).map(transacaoJpa -> {
            return new Transacao(
                    new TransacaoId(transacaoJpa.id),
                    null,
                    null,
                    transacaoJpa.tipo,
                    transacaoJpa.status,
                    transacaoJpa.data,
                    transacaoJpa.valor);
        }).orElse(null);
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