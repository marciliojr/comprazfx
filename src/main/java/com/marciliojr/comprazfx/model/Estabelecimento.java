package com.marciliojr.comprazfx.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "estabelecimento")
public class Estabelecimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeEstabelecimento;


    public Estabelecimento() {
    }

    public Estabelecimento(Long id, String nomeEstabelecimento) {
        this.id = id;
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Estabelecimento that = (Estabelecimento) o;
        return Objects.equals(id, that.id) && Objects.equals(nomeEstabelecimento, that.nomeEstabelecimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeEstabelecimento);
    }

    @Override
    public String toString() {
        return "Estabelecimento{" +
                "id=" + id +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                '}';
    }
}
