package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.model.dto.CompraDTO;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.service.ItemService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class VisualizarCupomController {

    @FXML
    private Label labelEstabelecimento;
    
    @FXML
    private Label labelData;
    
    @FXML
    private Label labelValorTotal;
    
    @FXML
    private TableView<ItemDTO> tabelaItens;
    
    @FXML
    private TableColumn<ItemDTO, String> colunaNome;
    
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaQuantidade;
    
    @FXML
    private TableColumn<ItemDTO, String> colunaUnidade;
    
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValorUnitario;
    
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValorTotal;
    
    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();
    private final ItemService itemService = SpringBootApp.context.getBean(ItemService.class);
    private Stage stage;
    
    @FXML
    private void initialize() {
        configurarTabela();
        tabelaItens.setItems(listaItens);
    }
    
    private void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colunaValorUnitario.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
    }
    
    public void setCompra(CompraDTO compra) {
        labelEstabelecimento.setText(compra.getNomeEstabelecimento());
        labelData.setText(compra.getDataCompraFormatada());
        labelValorTotal.setText(compra.getValorTotal().toString());
        
        carregarItens(compra.getId());
    }
    
    private void carregarItens(Long compraId) {
        List<ItemDTO> itens = itemService.listarItensPorCompraId(compraId);
        listaItens.clear();
        listaItens.addAll(itens);
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void fechar() {
        if (stage != null) {
            stage.close();
        }
    }
} 