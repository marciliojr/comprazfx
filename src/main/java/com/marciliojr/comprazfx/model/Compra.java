package com.marciliojr.comprazfx.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(nullable = false)
    private LocalDate dataCompra;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "compra")
    private List<Item> itens;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Compra compra = (Compra) o;
        return Objects.equals(id, compra.id) && Objects.equals(estabelecimento, compra.estabelecimento) && Objects.equals(dataCompra, compra.dataCompra) && Objects.equals(itens, compra.itens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, estabelecimento, dataCompra, itens);
    }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", estabelecimento=" + estabelecimento +
                ", dataCompra=" + dataCompra +
                ", itens=" + itens +
                '}';
    }
}
