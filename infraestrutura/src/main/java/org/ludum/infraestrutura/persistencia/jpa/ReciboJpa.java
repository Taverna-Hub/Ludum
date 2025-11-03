package org.ludum.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECIBO")
public class ReciboJpa {
  @Id
  String id;

  LocalDateTime data;

  BigDecimal valor;
}

interface ReciboJpaRepository extends JpaRepository<ReciboJpa, String> {

}
