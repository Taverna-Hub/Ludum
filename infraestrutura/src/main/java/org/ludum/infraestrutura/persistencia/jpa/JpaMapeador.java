package org.ludum.infraestrutura.persistencia.jpa;

import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
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
