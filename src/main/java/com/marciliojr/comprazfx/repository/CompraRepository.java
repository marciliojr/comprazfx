package com.marciliojr.comprazfx.repository;

import com.marciliojr.comprazfx.model.Compra;
import com.marciliojr.comprazfx.model.TipoCupom;
import com.marciliojr.comprazfx.model.dto.CompraDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.CompraDTO(" +
            "c.id, e.nomeEstabelecimento, c.dataCompra, SUM(i.valorTotal)) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " +
            "AND (:tipoCupom IS NULL OR e.tipoCupom = :tipoCupom) " +
            "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " +
            "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim)) " +
            "GROUP BY e.nomeEstabelecimento, c.dataCompra")
    List<CompraDTO> findByNomeEstabelecimentoDataCompraValorTotal(
            @Param("nomeEstabelecimento") String nomeEstabelecimento,
            @Param("tipoCupom") TipoCupom tipoCupom,
            @Param("dataInicio") String dataInicio,
            @Param("dataFim") String dataFim);

}
