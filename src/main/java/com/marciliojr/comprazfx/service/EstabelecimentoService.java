package com.marciliojr.comprazfx.service;

import com.marciliojr.comprazfx.model.Estabelecimento;
import com.marciliojr.comprazfx.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstabelecimentoService {

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService() {
    }

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAllEstabelecimento();
    }

    public void removerEstabelecimento(Long id) {
        estabelecimentoRepository.deleteById(id);
    }

    public Optional<Estabelecimento> obterEstabelecimento(String nome) {
        return estabelecimentoRepository.findByNomeEstabelecimento(nome);
    }
}
