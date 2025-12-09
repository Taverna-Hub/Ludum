package org.ludum.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "org.ludum")
@EntityScan(basePackages = "org.ludum.infraestrutura.persistencia.jpa")
@EnableJpaRepositories(basePackages = "org.ludum.infraestrutura.persistencia.jpa")
public class LudumApplication {

    public static void main(String[] args) {
        SpringApplication.run(LudumApplication.class, args);
    }
}
