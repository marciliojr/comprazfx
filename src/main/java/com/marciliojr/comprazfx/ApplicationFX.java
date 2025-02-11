package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.PDFExtractor;
import com.marciliojr.comprazfx.model.Estabelecimento;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.marciliojr.comprazfx.infra.ComprazUtils.parseDate;

public class ApplicationFX extends Application {

    private static final Logger logger = LogManager.getLogger(ApplicationFX.class);
    private static final String APP_TITLE = "Compraz - Gestor compras por nota fiscal";
    private static final String ERROR_MSG = "Erro ao carregar total";
    private static final int SPLASH_SCREEN_WIDTH = 800;
    private static final int SPLASH_SCREEN_HEIGHT = 600;
    private static final int SPLASH_SCREEN_DISPLAY_TIME = 3000;

    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();
    private final ObservableList<CompraDTO> listaCompras = FXCollections.observableArrayList();

    private final ItemService itemService = SpringBootApp.context.getBean(ItemService.class);
    private final PDFDataService pdfDataService = SpringBootApp.context.getBean(PDFDataService.class);
    private final PDFGenerationService pdfGenerationService = SpringBootApp.context.getBean(PDFGenerationService.class);
    private final CompraService compraService = SpringBootApp.context.getBean(CompraService.class);
    private final EstabelecimentoService estabelecimentoService = SpringBootApp.context.getBean(EstabelecimentoService.class);

    @FXML
    private Button carregarPdfButton;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private TextField nomeEstabelecimentoTextField;
    @FXML
    private TextField nomeEstabelecimentoTextFieldPesquisa;
    @FXML
    private TableView<ItemDTO> tabelaItens;
    @FXML
    private TableView<CompraDTO> tabelaCompras;
    @FXML
    private TableColumn<ItemDTO, String> colunaNome;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaQuantidade;
    @FXML
    private TableColumn<ItemDTO, String> colunaUnidade;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValor;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValorTotal;
    @FXML
    private TableColumn<ItemDTO, String> colunaEstabelecimento;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaDataCompra;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaValorTotalCompra;
    @FXML
    private TableColumn<ItemDTO, String> colunaEstabelecimentoCompra;
    @FXML
    private TableColumn<ItemDTO, BigDecimal> colunaDataCompraCompra;
    @FXML
    private DatePicker dataInicio;
    @FXML
    private DatePicker dataFim;
    @FXML
    private DatePicker dataCadastro;
    @FXML
    private Label somatorioValorItem;
    @FXML
    private DatePicker dataInicioCompras;
    @FXML
    private DatePicker dataFimCompras;
    @FXML
    private TextField nomeEstabelecimentoTextFieldPesquisaCompras;

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
        Scene scene = new Scene(fxmlLoader.load(), SPLASH_SCREEN_WIDTH, SPLASH_SCREEN_HEIGHT);
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
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
        configuraTabelaAbaItens();
        configuraTabelaAbaCompras();
        tabelaItens.setItems(listaItens);
        tabelaCompras.setItems(listaCompras);

        List<String> nomesEstabelecimentos = estabelecimentoService.listarTodos().stream()
                .map(Estabelecimento::getNomeEstabelecimento)
                .collect(Collectors.toList());

        TextFields.bindAutoCompletion(nomeEstabelecimentoTextField, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoTextFieldPesquisa, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoTextFieldPesquisaCompras, nomesEstabelecimentos);
    }

    private void configuraTabelaAbaCompras() {
        colunaEstabelecimentoCompra.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataCompraCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));
        colunaValorTotalCompra.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
    }

    private void configuraTabelaAbaItens() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colunaEstabelecimento.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));
    }

    @FXML
    private void pesquisar(ActionEvent event) {
        carregarItens();
        carregarValorSomatorio();
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
        pdfDataService.processarDadosEPersistir(pdfString, nomeEstabelecimentoTextField.getText(), parseDate(dataCadastro.getValue().toString()));
    }

    @FXML
    public void gerarPDF(ActionEvent event) throws IOException {
        BigDecimal somatorio = getSomatorio();
        byte[] pdfBytes = pdfGenerationService.generatePDF(new ArrayList<>(listaItens), somatorio.toString());

        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
        montarRelatorioPDF(pdfBytes);
    }

    private BigDecimal getSomatorio() {
        return itemService.somarValorUnitarioPorEstabelecimentoEPeriodo(
                getNomeEstabelecimento(nomeEstabelecimentoTextFieldPesquisa),
                parseDateToString(dataInicio),
                parseDateToString(dataFim));
    }

    private void montarRelatorioPDF(byte[] pdfBytes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("relatorio_compras_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
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
        String nomeEstabelecimento = getNomeEstabelecimento(nomeEstabelecimentoTextFieldPesquisaCompras);
        String dataInicioFormatada = parseDateToString(dataInicioCompras);
        String dataFimFormatada = parseDateToString(dataFimCompras);

        return compraService.listarComprasPorEstabelecimentoEPeriodo(nomeEstabelecimento, dataInicioFormatada, dataFimFormatada);
    }

    private void carregarItens() {
        Task<List<ItemDTO>> task = new Task<>() {
            @Override
            protected List<ItemDTO> call() {
                return getItensPorEstabelecimentoEPeriodo();
            }
        };
        task.setOnSucceeded(event -> listaItens.setAll(task.getValue()));
        new Thread(task).start();
    }

    private List<ItemDTO> getItensPorEstabelecimentoEPeriodo() {
        String nomeEstabelecimento = getNomeEstabelecimento(nomeEstabelecimentoTextFieldPesquisa);
        String dataInicioFormatada = parseDateToString(dataInicio);
        String dataFimFormatada = parseDateToString(dataFim);
        return itemService.listarItensPorEstabelecimentoEPeriodo(nomeEstabelecimento, dataInicioFormatada, dataFimFormatada);
    }

    private void carregarValorSomatorio() {
        Task<BigDecimal> task = new Task<>() {
            @Override
            protected BigDecimal call() {
                return getSomatorio();
            }
        };
        task.setOnSucceeded(event -> atualizarSomatorioLabel(task.getValue().toString()));
        new Thread(task).start();
    }

    private void atualizarSomatorioLabel(String valorString) {
        Platform.runLater(() -> {
            try {
                somatorioValorItem.setText("R$ " + configurarSomatorio(valorString));
            } catch (Exception e) {
                somatorioValorItem.setText(ERROR_MSG);
                logger.error("Erro ao converter resposta", e);
            }
        });
    }

    private String configurarSomatorio(String valorString) {
        BigDecimal somatorio = new BigDecimal(valorString);
        NumberFormat formatoMoeda = NumberFormat.getInstance(new Locale("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        return formatoMoeda.format(somatorio);
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
        nomeEstabelecimentoTextField.clear();
        file = null;
    }

    private boolean validarCadastro() {
        if (file == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Arquivo não selecionado", "Por favor, selecione um arquivo PDF antes de cadastrar.");
            return false;
        }

        String nomeEstabelecimento = nomeEstabelecimentoTextField.getText().trim();
        if (nomeEstabelecimento.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nome do Estabelecimento vazio", "Por favor, preencha o nome do estabelecimento.");
            return false;
        }

        return true;
    }

    private String parseDateToString(DatePicker datePicker) {
        return Objects.isNull(datePicker.getValue()) ? null : datePicker.getValue().format(DateTimeFormatter.ISO_DATE);
    }

    private String getNomeEstabelecimento(TextField textField) {
        return Strings.isBlank(textField.getText()) ? null : textField.getText();
    }
}