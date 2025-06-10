package com.fhfelipefh.projetofinal_lpoo_felipe.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int capacidade;

    @Column(nullable = false)
    private String localizacao;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal preco;

    public Sala() {}

    public Sala(String nome, int capacidade, String localizacao, BigDecimal preco) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.localizacao = localizacao;
        this.preco = preco;
    }

    // Getters and setters omitted for brevity

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sala sala)) return false;
        return Objects.equals(id, sala.id);
    }

    @Override
    public String toString() {
        return nome + " (" + capacidade + " pessoas)";
    }
}
