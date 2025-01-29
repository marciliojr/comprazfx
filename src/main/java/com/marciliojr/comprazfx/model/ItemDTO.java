package com.marciliojr.comprazfx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ItemDTO {

    private String nome;
    private BigDecimal quantidade;
    private String unidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private String nomeEstabelecimento;
    private LocalDate dataCompra;

    public ItemDTO() {
    }

    public ItemDTO(String nome, BigDecimal quantidade, String unidade, BigDecimal valorUnitario, BigDecimal valorTotal, String nomeEstabelecimento, LocalDate dataCompra) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.dataCompra = dataCompra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemDTO itemDTO = (ItemDTO) o;
        return Objects.equals(nome, itemDTO.nome) && Objects.equals(quantidade, itemDTO.quantidade) && Objects.equals(unidade, itemDTO.unidade) && Objects.equals(valorUnitario, itemDTO.valorUnitario) && Objects.equals(valorTotal, itemDTO.valorTotal) && Objects.equals(nomeEstabelecimento, itemDTO.nomeEstabelecimento) && Objects.equals(dataCompra, itemDTO.dataCompra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, quantidade, unidade, valorUnitario, valorTotal, nomeEstabelecimento, dataCompra);
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "nome='" + nome + '\'' +
                ", quantidade=" + quantidade +
                ", unidade='" + unidade + '\'' +
                ", valorUnitario=" + valorUnitario +
                ", valorTotal=" + valorTotal +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                ", dataCompra=" + dataCompra +
                '}';
    }
}