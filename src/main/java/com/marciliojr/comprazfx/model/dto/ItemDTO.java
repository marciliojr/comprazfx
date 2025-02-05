package com.marciliojr.comprazfx.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ItemDTO {

    private Long id;
    private String nome;
    private BigDecimal quantidade;
    private String unidade;
    private BigDecimal valorTotal;
    private BigDecimal valorUnitario;
    private LocalDate dataCompra;
    private String nomeEstabelecimento;

    public ItemDTO() {
    }

    public ItemDTO(Long id, String nome, BigDecimal quantidade, String unidade, BigDecimal valorTotal, BigDecimal valorUnitario, LocalDate dataCompra, String nomeEstabelecimento) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.valorTotal = valorTotal;
        this.valorUnitario = valorUnitario;
        this.dataCompra = dataCompra;
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public static ItemDTO construirComAtributosBasicos(String nome, BigDecimal quantidade, String unidade,
                                                       BigDecimal valorTotal, BigDecimal valorUnitario, LocalDate dataCompra) {
        ItemDTO item = new ItemDTO();
        item.setNome(nome);
        item.setQuantidade(quantidade);
        item.setUnidade(unidade);
        item.setDataCompra(dataCompra);
        item.setValorTotal(valorTotal);
        item.setValorUnitario(valorUnitario);
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
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
        ItemDTO itemDTO = (ItemDTO) o;
        return Objects.equals(id, itemDTO.id) && Objects.equals(nome, itemDTO.nome) && Objects.equals(quantidade, itemDTO.quantidade) && Objects.equals(unidade, itemDTO.unidade) && Objects.equals(valorTotal, itemDTO.valorTotal) && Objects.equals(valorUnitario, itemDTO.valorUnitario) && Objects.equals(dataCompra, itemDTO.dataCompra) && Objects.equals(nomeEstabelecimento, itemDTO.nomeEstabelecimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, quantidade, unidade, valorTotal, valorUnitario, dataCompra, nomeEstabelecimento);
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", quantidade=" + quantidade +
                ", unidade='" + unidade + '\'' +
                ", valorTotal=" + valorTotal +
                ", valorUnitario=" + valorUnitario +
                ", dataCompra=" + dataCompra +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                '}';
    }
}