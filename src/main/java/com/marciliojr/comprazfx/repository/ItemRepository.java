package com.marciliojr.comprazfx.repository;

import com.marciliojr.comprazfx.model.Item;
import com.marciliojr.comprazfx.model.TipoCupom;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.ItemDTO(" +
            "i.id, i.nome, i.quantidade, i.unidade, i.valorTotal, i.valorUnitario, " +
            "c.dataCompra, e.nomeEstabelecimento) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " +
            "AND (:tipoCupom IS NULL OR e.tipoCupom = :tipoCupom) " +
            "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " +
            "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim))")
    List<ItemDTO> findAllItemsByEstabelecimentoAndPeriodo(
            @Param("nomeEstabelecimento") String nomeEstabelecimento,
            @Param("tipoCupom") TipoCupom tipoCupom,
            @Param("dataInicio") String dataInicio,
            @Param("dataFim") String dataFim);

    @Query("SELECT COALESCE(SUM(i.valorTotal), 0) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " +
            "AND (:tipoCupom IS NULL OR e.tipoCupom = :tipoCupom) " +
            "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " +
            "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim))")
    BigDecimal sumValorTotalByEstabelecimentoAndPeriodo(
            @Param("nomeEstabelecimento") String nomeEstabelecimento,
            @Param("tipoCupom") TipoCupom tipoCupom,
            @Param("dataInicio") String dataInicio,
            @Param("dataFim") String dataFim);

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.ItemDTO(" +
            "i.id, i.nome, i.quantidade, i.unidade, i.valorTotal, i.valorUnitario, " +
            "c.dataCompra, e.nomeEstabelecimento) " +
            "FROM Item i " +
            "JOIN i.compra c " +
            "JOIN c.estabelecimento e " +
            "WHERE (:nome IS NULL OR LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:tipoCupom IS NULL OR e.tipoCupom = :tipoCupom) " +
            "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " +
            "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim))")
    List<ItemDTO> findByNomeByPeriodo(
            @Param("nome") String nome,
            @Param("tipoCupom") TipoCupom tipoCupom,
            @Param("dataInicio") String dataInicio,
            @Param("dataFim") String dataFim);

    void deleteById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Item i WHERE i.compra.id = :compraId")
    void deleteByCompraId(@Param("compraId") Long compraId);
}
