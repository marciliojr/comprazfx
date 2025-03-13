package com.marciliojr.comprazfx.service;

import com.marciliojr.comprazfx.infra.ItemNormalizer;
import com.marciliojr.comprazfx.model.Item;
import com.marciliojr.comprazfx.model.TipoCupom;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.repository.ItemRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
                .map(item -> ItemDTO.construir(item.getNome(), item.getQuantidade(), item.getUnidade(),
                        item.getValorTotal(), item.getValorUnitario(), item.getCompra().getDataCompra(),
                        item.getCompra().getEstabelecimento().getNomeEstabelecimento()))
                .collect(Collectors.toList());
    }

    public List<ItemDTO> listarItensPorEstabelecimentoEPeriodo(String nomeEstabelecimento, TipoCupom tipoCupom, String dataInicio, String dataFim) {
        if (Objects.isNull(dataInicio) && Objects.isNull(dataFim) && Strings.isBlank(nomeEstabelecimento) && tipoCupom == null) {
            return listarTodos();
        }

        return itemRepository.findAllItemsByEstabelecimentoAndPeriodo(nomeEstabelecimento, tipoCupom, dataInicio, dataFim);
    }

    public List<ItemDTO> listarItensPorCompraId(Long compraId) {
        return itemRepository.findByCompraId(compraId);
    }

    public BigDecimal somarValorUnitarioPorEstabelecimentoEPeriodo(String nomeEstabelecimento, TipoCupom tipoCupom, String dataInicio, String dataFim) {
        return itemRepository.sumValorTotalByEstabelecimentoAndPeriodo(nomeEstabelecimento, tipoCupom, dataInicio, dataFim);
    }

    public List<ItemDTO> listarItensPorNomeEPeriodo(String nome, TipoCupom tipoCupom, String dataInicio, String dataFim, String nomeEstabelecimento) {
        return itemRepository.findByNomeByPeriodo(nome, tipoCupom, dataInicio, dataFim, nomeEstabelecimento);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public void deleteByCompraId(Long compraId) {
        itemRepository.deleteByCompraId(compraId);
    }

    public void atualizarItem(ItemDTO itemDTO) {
        Item item = itemRepository.findById(itemDTO.getId())
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        item.setNome(itemDTO.getNome());
        item.setQuantidade(itemDTO.getQuantidade());
        item.setUnidade(itemDTO.getUnidade());
        item.setValorUnitario(itemDTO.getValorUnitario());
        item.setValorTotal(itemDTO.getValorTotal());

        itemRepository.save(item);
    }

    public void normalizarNomes() {
        List<Item> all = itemRepository.findAll();
        ItemNormalizer.normalizarNomes(all);
        for (Item item : all) {
            itemRepository.save(item);
        }
    }
}
