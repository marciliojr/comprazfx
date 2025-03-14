package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.PDFExtractor;
import com.marciliojr.comprazfx.model.Estabelecimento;
import com.marciliojr.comprazfx.model.TipoCupom;
import com.marciliojr.comprazfx.model.dto.CompraDTO;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.service.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.marciliojr.comprazfx.infra.ComprazUtils.parseDate;

public class ApplicationFX extends Application {
    private static final Logger logger = LogManager.getLogger(ApplicationFX.class);
    private static final String APP_TITLE = "ComprazFx - Gestor compras por cupom fiscal";
    private static final int SPLASH_SCREEN_WIDTH = 800;
    private static final int SPLASH_SCREEN_HEIGHT = 600;
    private static final int SPLASH_SCREEN_DISPLAY_TIME = 3000;
    private final ObservableList<CompraDTO> listaCompras = FXCollections.observableArrayList();
    private final ObservableList<ItemDTO> listaProdutos = FXCollections.observableArrayList();
    private final ItemService itemService = SpringBootApp.context.getBean(ItemService.class);
    private final PDFDadosService pdfDadosService = SpringBootApp.context.getBean(PDFDadosService.class);
    private final PDFGeradorProdutos pdfGeradorProdutos = SpringBootApp.context.getBean(PDFGeradorProdutos.class);
    private final CompraService compraService = SpringBootApp.context.getBean(CompraService.class);
    private final EstabelecimentoService estabelecimentoService = SpringBootApp.context.getBean(EstabelecimentoService.class);
    private String currentUserCSS;
    @FXML
    private Button carregarPdfButton;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private TextField nomeEstabelecimentoCadastro;
    @FXML
    private TextField nomeEstabelecimentoPesquisaProdutos;
    @FXML
    private TextField nomeEstabelecimentoPesquisaCupons;
    @FXML
    private TextField nomeProdutoTextField;
    @FXML
    private TableView<CompraDTO> tabelaCompras;
    @FXML
    private TableView<ItemDTO> tabelaProduto;
    @FXML
    private TableColumn<ItemDTO, String> colunaNomeProduto;
    @FXML
    private TableColumn<ItemDTO, String> colunaNomeEstabelecimentoProduto;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaDataProduto;
    @FXML
    private TableColumn<ItemDTO, String> colunaValorTotalProduto;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValorTotalCompra;
    @FXML
    private TableColumn<ItemDTO, String> colunaEstabelecimentoCompra;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaDataCompraCompra;
    @FXML
    private DatePicker dataInicioCupons;
    @FXML
    private DatePicker dataFimCupons;
    @FXML
    private DatePicker dataInicioProdutos;
    @FXML
    private DatePicker dataFimProdutos;
    @FXML
    private ComboBox<String> comboBoxCss;
    @FXML
    private ComboBox<TipoCupom> comboTipoCupom;
    @FXML
    private ComboBox<TipoCupom> tipoCupomComboCupons;
    @FXML
    private DatePicker dataCadastro;
    @FXML
    private ComboBox<TipoCupom> tipoCupomComboProdutos;
    private File file;

    @Override
    public void start(Stage stage) throws IOException {
        Stage splashStage = createSplashScreen();
        splashStage.show();
        new Thread(() -> {
            try {
                Thread.sleep(SPLASH_SCREEN_DISPLAY_TIME);
            } catch (InterruptedException e) {
                logger.error("Erro ao carregar splash screen", e);
            }
            Platform.runLater(() -> {
                splashStage.close();
                try {
                    initializeMainStage(stage);
                } catch (IOException e) {
                    logger.error("Erro ao inicializar a janela principal", e);
                }
            });
        }).start();
    }

    private void initializeMainStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationFX.class.getResource("main-view.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load(), SPLASH_SCREEN_WIDTH, SPLASH_SCREEN_HEIGHT);
        String cssArquivo = lerPropriedade("estilo", "compraz.css");
        currentUserCSS = getClass().getResource("/css/" + cssArquivo).toExternalForm();
        mainScene.getStylesheets().add(currentUserCSS);
        stage.setScene(mainScene);
        stage.setTitle(APP_TITLE);
        stage.setOnCloseRequest(this::handleCloseRequest);
        stage.show();
    }

    private void handleCloseRequest(WindowEvent event) {
        if (SpringBootApp.context != null) {
            SpringBootApp.context.close();
        }
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void initialize() {
        configuraTabelaAbaCompras();
        configuraTabelaAbaProdutos();
        tabelaCompras.setItems(listaCompras);
        tabelaProduto.setItems(listaProdutos);
        List<String> nomesEstabelecimentos = estabelecimentoService.listarTodos().stream().map(Estabelecimento::getNomeEstabelecimento).collect(Collectors.toList());
        TextFields.bindAutoCompletion(nomeEstabelecimentoCadastro, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaProdutos, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaCupons, nomesEstabelecimentos);

        tabelaCompras.setRowFactory(tv -> {
            TableRow<CompraDTO> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem viewItem = new MenuItem("Visualizar Cupom");
            MenuItem deleteItem = new MenuItem("Deletar Cupom");
            viewItem.setOnAction(event -> visualizarCupom(row.getItem()));
            deleteItem.setOnAction(event -> excluirCompra(row.getItem()));
            contextMenu.getItems().addAll(viewItem, deleteItem);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        tabelaProduto.setRowFactory(tv -> {
            TableRow<ItemDTO> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Alterar");
            MenuItem deleteItem = new MenuItem("Excluir");
            editItem.setOnAction(event -> abrirTelaEdicao(row.getItem()));
            deleteItem.setOnAction(event -> excluirItem(row.getItem()));
            contextMenu.getItems().addAll(editItem, deleteItem);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        carregarListaCss();
        carregarComboTipoCupom();
        tipoCupomComboProdutos.setItems(FXCollections.observableArrayList(Arrays.asList(TipoCupom.values())));
        tipoCupomComboProdutos.setValue(TipoCupom.TODOS);
    }

    private void carregarComboTipoCupom() {
        comboTipoCupom.getItems().clear();
        comboTipoCupom.getItems().addAll(Arrays.asList(TipoCupom.values()));
        comboTipoCupom.setValue(TipoCupom.TODOS);
    }

    @FXML
    public void aplicarEstilo() {
        String cssSelecionado = comboBoxCss.getValue();
        if (cssSelecionado == null || cssSelecionado.isEmpty()) {
            return;
        }
        String novoCSS = getClass().getResource("/css/" + cssSelecionado).toExternalForm();
        
        // Aplicar o CSS a todas as cenas abertas
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Scene cenaAtual = ((Stage) window).getScene();
                if (cenaAtual != null) {
                    if (currentUserCSS != null) {
                        cenaAtual.getStylesheets().remove(currentUserCSS);
                    }
                    cenaAtual.getStylesheets().add(novoCSS);
                }
            }
        }
        currentUserCSS = novoCSS;
        atualizarPropriedade("estilo", cssSelecionado);
    }

    private void atualizarPropriedade(String chave, String valor) {
        Properties props = new Properties();
        File file = new File("config.properties");
        try {
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                }
            }
            props.setProperty(chave, valor);
            try (FileOutputStream out = new FileOutputStream(file)) {
                props.store(out, "Configurações de CSS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lerPropriedade(String chave, String valorPadrao) {
        Properties props = new Properties();
        File file = new File("config.properties");
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                return props.getProperty(chave, valorPadrao);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return valorPadrao;
    }

    private void carregarListaCss() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/css/*.css");
            List<String> listaNomes = new ArrayList<>();
            for (Resource resource : resources) {
                listaNomes.add(resource.getFilename());
            }
            comboBoxCss.getItems().clear();
            comboBoxCss.getItems().addAll(listaNomes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configuraTabelaAbaCompras() {
        colunaEstabelecimentoCompra.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataCompraCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompraFormatada"));
        colunaValorTotalCompra.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
    }

    private void configuraTabelaAbaProdutos() {
        colunaNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaNomeEstabelecimentoProduto.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataProduto.setCellValueFactory(new PropertyValueFactory<>("dataCompraFormatada"));
        colunaValorTotalProduto.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
    }

    @FXML
    private void carregarPdf(ActionEvent event) {
        file = abrirFileChooser("Arquivos PDF", "*.pdf");
        if (file != null) {
            atualizarCampoTexto(fileNameTextField, file.getName());
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum arquivo selecionado", "Por favor, selecione um arquivo PDF.");
        }
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        if (!validarCadastro()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar", "Ocorreu um erro ao tentar cadastrar o arquivo.");
            return;
        }
        try {
            processarCadastro();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Arquivo cadastrado com sucesso!");
            limparCampos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar", "Ocorreu um erro ao tentar cadastrar o arquivo: " + e.getMessage());
        }
    }

    private void processarCadastro() throws IOException {
        PDFExtractor pdfExtractor = new PDFExtractor();
        String pdfString = pdfExtractor.extrairTextoPDF(file);
        pdfDadosService.processarDadosEPersistir(pdfString, nomeEstabelecimentoCadastro.getText(), parseDate(dataCadastro.getValue().toString()), comboTipoCupom.getValue());
    }


    @FXML
    public void gerarPDFProduto(ActionEvent event) throws IOException {
        BigDecimal somatorio = getSomatorio();
        byte[] pdfBytes = pdfGeradorProdutos.generatePDF(new ArrayList<>(listaProdutos), somatorio.toString());
        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
        montarRelatorioPDF(pdfBytes, "produtos");
    }

    private BigDecimal getSomatorio() {
        String nomeEstabelecimento = getNome(nomeEstabelecimentoPesquisaProdutos);
        TipoCupom tipoCupom = getTipoCupomSelecionado(tipoCupomComboProdutos);
        String dataInicioFormatada = parseDateToString(dataInicioProdutos);
        String dataFimFormatada = parseDateToString(dataFimProdutos);
        return itemService.somarValorUnitarioPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicioFormatada, dataFimFormatada);
    }

    private void montarRelatorioPDF(byte[] pdfBytes, String tipoRelatorio) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("relatorio_" + tipoRelatorio + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
                mostrarMensagem("Sucesso!", "PDF gerado e salvo com sucesso.");
            } catch (IOException e) {
                mostrarMensagem("Erro!", "Não foi possível salvar o PDF.");
            }
        }
    }

    @FXML
    public void pesquisarProdutos(ActionEvent event) {
        String nomeProduto = nomeProdutoTextField.getText();
        String nomeEstabelecimento = nomeEstabelecimentoPesquisaProdutos.getText();
        TipoCupom tipoCupom = getTipoCupomSelecionado(tipoCupomComboProdutos);
        String dataInicio = dataInicioProdutos.getValue() != null ? dataInicioProdutos.getValue().toString() : null;
        String dataFim = dataFimProdutos.getValue() != null ? dataFimProdutos.getValue().toString() : null;

        listaProdutos.clear();
        listaProdutos.addAll(itemService.listarItensPorNomeEPeriodo(nomeProduto, tipoCupom, dataInicio, dataFim, nomeEstabelecimento));
    }

    @FXML
    public void pesquisarCompras(ActionEvent event) {
        carregarCompras();
    }

    private void carregarCompras() {
        Task<List<CompraDTO>> task = new Task<>() {
            @Override
            protected List<CompraDTO> call() {
                return getComprasPorEstabelecimentoEPeriodo();
            }
        };
        task.setOnSucceeded(event -> listaCompras.setAll(task.getValue()));
        new Thread(task).start();
    }

    private List<CompraDTO> getComprasPorEstabelecimentoEPeriodo() {
        String nomeEstabelecimento = getNome(nomeEstabelecimentoPesquisaCupons);
        TipoCupom tipoCupom = getTipoCupomSelecionado(tipoCupomComboCupons);
        String dataInicioFormatada = parseDateToString(dataInicioCupons);
        String dataFimFormatada = parseDateToString(dataFimCupons);
        return compraService.listarComprasPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicioFormatada, dataFimFormatada);
    }

    @FXML
    public void acaoBotaoNormalizar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Normalizar nomes de Produtos?");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza de que deseja Normalizar todos os nomes de produtos ja cadastrados? Essa Ação não pode ser desfeita.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    itemService.normalizarNomes();
                    mostrarMensagem("Sucesso", "Nomes dos produtos foram normalizados com sucesso.");
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro ao normalizar", "Ocorreu um erro ao tentar normalizar os dados: " + e.getMessage());
                }
            }
        });
    }

    private Stage createSplashScreen() {
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        StackPane splashRoot = new StackPane();
        ImageView splashImage = new ImageView(new Image(getClass().getResource("/imagens/splash.png").toExternalForm()));
        splashImage.setFitWidth(SPLASH_SCREEN_WIDTH);
        splashImage.setFitHeight(SPLASH_SCREEN_HEIGHT);
        splashRoot.getChildren().add(splashImage);
        Scene splashScene = new Scene(splashRoot, SPLASH_SCREEN_WIDTH, SPLASH_SCREEN_HEIGHT);
        splashStage.setScene(splashScene);
        return splashStage;
    }

    private File abrirFileChooser(String descricao, String... extensoes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(descricao, extensoes));
        Stage stage = (Stage) carregarPdfButton.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    private void atualizarCampoTexto(TextField campo, String texto) {
        campo.setText(texto);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, mensagem);
    }

    private void limparCampos() {
        fileNameTextField.clear();
        nomeEstabelecimentoCadastro.clear();
        file = null;
    }

    private boolean validarCadastro() {
        if (file == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Arquivo não selecionado", "Por favor, selecione um arquivo PDF antes de cadastrar.");
            return false;
        }
        String nomeEstabelecimento = nomeEstabelecimentoCadastro.getText().trim();
        if (nomeEstabelecimento.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nome do Estabelecimento vazio", "Por favor, preencha o nome do estabelecimento.");
            return false;
        }
        return true;
    }

    private String parseDateToString(DatePicker datePicker) {
        return Objects.isNull(datePicker.getValue()) ? null : datePicker.getValue().format(DateTimeFormatter.ISO_DATE);
    }

    private String getNome(TextField textField) {
        return Strings.isBlank(textField.getText()) ? null : textField.getText();
    }

    private void excluirCompra(CompraDTO compra) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Cupom");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza de que deseja excluir este cupom ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                itemService.deleteByCompraId(compra.getId());
                compraService.excluirCompraPorId(compra.getId());
                listaCompras.remove(compra);
                mostrarMensagem("Sucesso", "Cupom excluído com sucesso.");
            }
        });
    }

    private TipoCupom getTipoCupomSelecionado(ComboBox<TipoCupom> combo) {
        TipoCupom valor = combo.getValue();
        return valor == null || valor == TipoCupom.TODOS ? null : valor;
    }

    @FXML
    private void abrirTelaEdicao(ItemDTO item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editar-item.fxml"));

            loader.setControllerFactory(SpringBootApp.context::getBean);

            Parent root = loader.load();

            EditarItemController controller = loader.getController();
            controller.setItem(item);
            controller.setCallback(() -> {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Item atualizado com sucesso! Atualize a pesquisa.");
                pesquisarProdutos(null);
            });

            Stage stage = new Stage();
            stage.setTitle("Editar Item");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao abrir tela de edição: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void excluirItem(ItemDTO item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Item");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza de que deseja excluir este item?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    itemService.deleteById(item.getId());
                    listaProdutos.remove(item);
                    mostrarMensagem("Sucesso", "Item excluído com sucesso.");
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir item: " + e.getMessage());
                }
            }
        });
    }

    private void visualizarCupom(CompraDTO compra) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("visualizar-cupom.fxml"));
            Parent root = loader.load();
            
            VisualizarCupomController controller = loader.getController();
            controller.setCompra(compra);
            
            Stage stage = new Stage();
            stage.setTitle("Visualizar Cupom - " + compra.getNomeEstabelecimento());
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir visualização", "Ocorreu um erro ao tentar abrir a visualização do cupom: " + e.getMessage());
        }
    }
}
