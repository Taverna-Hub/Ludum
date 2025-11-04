package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Entity
@Table(name = "CARTEIRA")
class CarteiraJpa {
	@Id
	String id;

	BigDecimal disponivel;

	BigDecimal bloqueado;

	boolean contaExternaValida;
}

interface CarteiraJpaRepository extends JpaRepository<CarteiraJpa, String> {
}

@Repository
class CarteiraRepositoryImpl implements CarteiraRepository {
	@Autowired
	CarteiraJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Carteira carteira) {
		var carteiraJpa = mapeador.map(carteira, CarteiraJpa.class);
		repositorio.save(carteiraJpa);
	}

	@Override
	public Carteira obterPorContaId(ContaId contaId) {
		var carteira = repositorio.findById(contaId.getValue());
		return mapeador.map(carteira, Carteira.class);
	}
}
