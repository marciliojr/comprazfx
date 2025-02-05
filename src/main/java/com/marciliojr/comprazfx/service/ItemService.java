package com.marciliojr.comprazfx.service;

import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public ItemService() {
    }

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDTO> listarTodos() {
        return itemRepository.findAll().stream()
                .map(item -> ItemDTO.construirComAtributosBasicos(item.getNome(), item.getQuantidade(), item.getUnidade(), item.getValorTotal(), item.getValorUnitario(), item.getCompra().getDataCompra()))
                .collect(Collectors.toList());
    }

    public List<ItemDTO> listarItensPorEstabelecimentoEPeriodo(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return itemRepository.findAllItemsByEstabelecimentoAndPeriodo(nomeEstabelecimento, dataInicio, dataFim);
    }

    public BigDecimal somarValorUnitarioPorEstabelecimentoEPeriodo(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return itemRepository.sumValorTotalByEstabelecimentoAndPeriodo(nomeEstabelecimento, dataInicio, dataFim);
    }


}
