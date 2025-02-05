package com.marciliojr.comprazfx.repository;


import com.marciliojr.comprazfx.model.Item;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.ItemDTO(" +
            "i.id, i.nome, i.quantidade, i.unidade,i.valorTotal, i.valorUnitario, " +
            "c.dataCompra, e.nomeEstabelecimento) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " +
            "AND (:dataInicio IS NULL OR :dataFim IS NULL OR c.dataCompra BETWEEN :dataInicio AND :dataFim)")
    List<ItemDTO> findAllItemsByEstabelecimentoAndPeriodo(
            @Param("nomeEstabelecimento") String nomeEstabelecimento,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT COALESCE(SUM(i.valorTotal), 0) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " +
            "AND (:dataInicio IS NOT NULL AND c.dataCompra >= :dataInicio OR :dataInicio IS NULL) " +
            "AND (:dataFim IS NOT NULL AND c.dataCompra <= :dataFim OR :dataFim IS NULL)")
    BigDecimal sumValorTotalByEstabelecimentoAndPeriodo(
            @Param("nomeEstabelecimento") String nomeEstabelecimento,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );


}
