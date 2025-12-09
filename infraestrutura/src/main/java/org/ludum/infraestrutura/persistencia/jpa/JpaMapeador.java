package org.ludum.infraestrutura.persistencia.jpa;

import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.entidades.ReviewId;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.entities.SeguimentoId;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;

@Component
public class JpaMapeador extends ModelMapper {

  JpaMapeador() {
    var configuracao = getConfiguration();
    configuracao.setFieldMatchingEnabled(true);
    configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

    addConverter(new AbstractConverter<CarteiraJpa, Carteira>() {
      @Override
      protected Carteira convert(CarteiraJpa source) {
        var id = map(source.id, ContaId.class);
        Saldo saldo = new Saldo(source.disponivel, source.bloqueado);
        var carteira = new Carteira(id, saldo);

        return carteira;
      }
    });

    addConverter(new AbstractConverter<ReviewJpa, Review>() {
      @Override
      protected Review convert(ReviewJpa source) {
        var id = new ReviewId(source.id);
        var jogoId = new JogoId(source.jogoId);
        var autorId = new ContaId(source.autorId);
        return new Review(id, jogoId, autorId, source.nota, source.titulo, source.texto, source.data, source.isRecomendado, source.status);
      }
    });

    addConverter(new AbstractConverter<Review, ReviewJpa>() {
      @Override
      protected ReviewJpa convert(Review source) {
        var jpa = new ReviewJpa();
        jpa.id = source.getId().getValue();
        jpa.jogoId = source.getJogoId().getValue();
        jpa.autorId = source.getAutorId().getValue();
        jpa.nota = source.getNota();
        jpa.titulo = source.getTitulo();
        jpa.texto = source.getTexto();
        jpa.data = source.getData();
        jpa.dataUltimaEdicao = source.getDataUltimaEdicao();
        jpa.isRecomendado = source.isRecomendado();
        jpa.status = source.getStatus();
        return jpa;
      }
    });

    addConverter(new AbstractConverter<SeguimentoJpa, Seguimento>() {
      @Override
      protected Seguimento convert(SeguimentoJpa source) {
        var id = new SeguimentoId(source.id);
        var seguidorId = new ContaId(source.seguidorId);
        var seguidoId = new AlvoId(source.seguidoId);
        return new Seguimento(id, seguidorId, seguidoId, source.tipoAlvo);
      }
    });

    addConverter(new AbstractConverter<Seguimento, SeguimentoJpa>() {
      @Override
      protected SeguimentoJpa convert(Seguimento source) {
        var jpa = new SeguimentoJpa();
        jpa.id = source.getId().getValue();
        jpa.seguidorId = source.getSeguidorId().getValue();
        jpa.seguidoId = source.getSeguidoId().getValue();
        jpa.tipoAlvo = source.getTipoAlvo();
        return jpa;
      }
    });

    // TODO: Adicionar quando Conta estiver funcionando
    // addConverter(new AbstractConverter<TransacaoJpa, Transacao>() {
    // @Override
    // protected Transacao convert(TransacaoJpa source) {
    // var id = map(source.id, TransacaoId.class);
    // var transacao = new Transacao(source.id, source.);

    // }
    // });
  }

  @Override
  public <D> D map(Object source, Class<D> destinationType) {
    return source != null ? super.map(source, destinationType) : null;
  }
}
