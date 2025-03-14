package com.marciliojr.comprazfx.service;

import com.marciliojr.comprazfx.model.TipoCupom;
import com.marciliojr.comprazfx.model.dto.CompraDTO;
import com.marciliojr.comprazfx.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public CompraService() {
    }

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<CompraDTO> listarComprasPorEstabelecimentoEPeriodo(String nomeEstabelecimento, TipoCupom tipoCupom, String dataInicio, String dataFim) {
        return compraRepository.findByNomeEstabelecimentoDataCompraValorTotal(nomeEstabelecimento, tipoCupom, dataInicio, dataFim);
    }

    public void excluirCompraPorId(Long id) {
        compraRepository.deleteById(id);
    }

}
