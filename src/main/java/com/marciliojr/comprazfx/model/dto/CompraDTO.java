package com.marciliojr.comprazfx.model.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class CompraDTO {

    private String nomeEstabelecimento;
    private String dataCompra;
    private BigDecimal valorTotal;

    public CompraDTO() {
    }

    public CompraDTO(String nomeEstabelecimento, String dataCompra, BigDecimal valorTotal) {
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.dataCompra = dataCompra;
        this.valorTotal = valorTotal;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public String getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompraDTO compraDTO = (CompraDTO) o;
        return Objects.equals(nomeEstabelecimento, compraDTO.nomeEstabelecimento) && Objects.equals(dataCompra, compraDTO.dataCompra) && Objects.equals(valorTotal, compraDTO.valorTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeEstabelecimento, dataCompra, valorTotal);
    }

    @Override
    public String toString() {
        return "CompraDTO{" +
                "nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                ", dataCompra='" + dataCompra + '\'' +
                ", valorTotal='" + valorTotal + '\'' +
                '}';
    }
}
