package com.marciliojr.comprazfx.repository;


import com.marciliojr.comprazfx.model.Compra;
import com.marciliojr.comprazfx.model.dto.CompraDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT new com.marciliojr.comprazfx.model.dto.CompraDTO(" + "e.nomeEstabelecimento, c.dataCompra, SUM(i.valorTotal)) " + "FROM Item i " + "JOIN i.compra c " + "JOIN c.estabelecimento e " + "WHERE (:nomeEstabelecimento IS NULL OR e.nomeEstabelecimento = :nomeEstabelecimento) " + "AND (:dataInicio IS NULL OR DATE(c.dataCompra) >= DATE(:dataInicio)) " + "AND (:dataFim IS NULL OR DATE(c.dataCompra) <= DATE(:dataFim)) GROUP BY e.nomeEstabelecimento, c.dataCompra")
    List<CompraDTO> findByNomeEstabelecimentoDataCompraValorTotal(String nomeEstabelecimento, String dataInicio, String dataFim);

}
