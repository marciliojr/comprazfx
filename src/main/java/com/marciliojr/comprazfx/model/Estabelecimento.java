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

    @Enumerated(EnumType.STRING)
    private TipoCupom tipoCupom;

    public Estabelecimento() {
    }

    public Estabelecimento(Long id, String nomeEstabelecimento, TipoCupom tipoCupom) {
        this.id = id;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.tipoCupom = tipoCupom;
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

    public TipoCupom getTipoCupom() {
        return tipoCupom;
    }

    public void setTipoCupom(TipoCupom tipoCupom) {
        this.tipoCupom = tipoCupom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estabelecimento that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(nomeEstabelecimento, that.nomeEstabelecimento) &&
                tipoCupom == that.tipoCupom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeEstabelecimento, tipoCupom);
    }

    @Override
    public String toString() {
        return "Estabelecimento{" +
                "id=" + id +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                ", tipoCupom=" + tipoCupom +
                '}';
    }
}
