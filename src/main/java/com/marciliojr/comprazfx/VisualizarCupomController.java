package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.model.dto.CompraDTO;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.service.ItemService;
import com.marciliojr.comprazfx.service.PDFGeradorProdutos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private final PDFGeradorProdutos pdfGeradorProdutos = SpringBootApp.context.getBean(PDFGeradorProdutos.class);
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

    @FXML
    private void gerarPDF() {
        try {
            BigDecimal somatorio = listaItens.stream().map(ItemDTO::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            byte[] pdfBytes = pdfGeradorProdutos.generatePDF(new ArrayList<>(listaItens), somatorio.toString());
            if (pdfBytes == null || pdfBytes.length == 0) {
                mostrarMensagem("Erro!", "Erro ao gerar o PDF.");
                return;
            }
            salvarPDF(pdfBytes);
        } catch (IOException e) {
            mostrarMensagem("Erro!", "Erro ao gerar o PDF: " + e.getMessage());
        }
    }

    private void salvarPDF(byte[] pdfBytes) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("cupom_" + labelEstabelecimento.getText() + ".pdf");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
                mostrarMensagem("Sucesso!", "PDF gerado e salvo com sucesso.");
            } catch (IOException e) {
                mostrarMensagem("Erro!", "Não foi possível salvar o PDF.");
            }
        }
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 