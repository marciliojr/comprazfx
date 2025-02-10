package com.marciliojr.comprazfx.repository;


import com.marciliojr.comprazfx.model.Item;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.ItemDTO(" + "i.id, i.nome, i.quantidade, i.unidade, i.valorTotal, i.valorUnitario, " + "c.dataCompra, e.nomeEstabelecimento) " + "FROM Item i " + "JOIN i.compra c " + "JOIN c.estabelecimento e " + "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " + "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " + "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim))")
    List<ItemDTO> findAllItemsByEstabelecimentoAndPeriodo(@Param("nomeEstabelecimento") String nomeEstabelecimento, @Param("dataInicio") String dataInicio, @Param("dataFim") String dataFim);

    @Query("SELECT COALESCE(SUM(i.valorTotal), 0) " + "FROM Item i " + "JOIN i.compra c " + "JOIN c.estabelecimento e " + "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " + "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " + "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim))")
    BigDecimal sumValorTotalByEstabelecimentoAndPeriodo(@Param("nomeEstabelecimento") String nomeEstabelecimento, @Param("dataInicio") String dataInicio, @Param("dataFim") String dataFim);

}
