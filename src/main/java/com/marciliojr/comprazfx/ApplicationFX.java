package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.PDFExtractor;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.service.ItemService;
import com.marciliojr.comprazfx.service.PDFDataService;
import com.marciliojr.comprazfx.service.PDFGenerationService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.apache.logging.log4j.util.Strings;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.marciliojr.comprazfx.infra.ComprazUtils.parseDate;

public class ApplicationFX extends Application {

    private static final String APP_TITLE = "Compraz - Gestor compras por nota fiscal";
    private static final String ERROR_MSG = "Erro ao carregar total";
    private static final int SPLASH_SCREEN_WIDTH = 800;
    private static final int SPLASH_SCREEN_HEIGHT = 600;
    private static final int SPLASH_SCREEN_DISPLAY_TIME = 3000;
    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();
    private final ItemService itemService = SpringBootApp.context.getBean(ItemService.class);
    private final PDFDataService pdfDataService = SpringBootApp.context.getBean(PDFDataService.class);
    private final PDFGenerationService pdfGenerationService = SpringBootApp.context.getBean(PDFGenerationService.class);
    private BigDecimal somatorio = BigDecimal.ZERO;
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
    private DatePicker dataInicio;
    @FXML
    private DatePicker dataFim;
    @FXML
    private DatePicker dataCadastro;
    @FXML
    private Label somatorioValorItem;

    private File file;

    @Override
    public void start(Stage stage) throws IOException {
        Stage splashStage = createSplashScreen();
        splashStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(SPLASH_SCREEN_DISPLAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                splashStage.close();
                try {
                    initializeMainStage(stage);
                } catch (IOException e) {
                    e.printStackTrace();
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
        configureTableColumns();
        tabelaItens.setItems(listaItens);
    }

    private void configureTableColumns() {
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
        byte[] pdfBytes = pdfGenerationService.generatePDF(new ArrayList<>(listaItens), somatorio.toString());

        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
        montarRelatorioPDF(pdfBytes);
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

    private void carregarItens() {
        Platform.runLater(() -> {
            listaItens.clear();
            listaItens.addAll(getItensPorEstabelecimentoEPeriodo());
        });
    }

    private List<ItemDTO> getItensPorEstabelecimentoEPeriodo() {
        String nomeEstabelecimento = Strings.isBlank(nomeEstabelecimentoTextFieldPesquisa.getText()) ? null : nomeEstabelecimentoTextFieldPesquisa.getText();
        String dataInicioFormatada = Objects.isNull(dataInicio.getValue()) ? null : dataInicio.getValue().format(DateTimeFormatter.ISO_DATE);
        String dataFimFormatada = Objects.isNull(dataFim.getValue()) ? null : dataFim.getValue().format(DateTimeFormatter.ISO_DATE);
        return itemService.listarItensPorEstabelecimentoEPeriodo(nomeEstabelecimento, dataInicioFormatada, dataFimFormatada);
    }

    private void carregarValorSomatorio() {
        somatorio = itemService.somarValorUnitarioPorEstabelecimentoEPeriodo(Strings.isBlank(nomeEstabelecimentoTextFieldPesquisa.getText()) ? null : nomeEstabelecimentoTextFieldPesquisa.getText(), Objects.isNull(dataInicio.getValue()) ? null : dataInicio.getValue().format(DateTimeFormatter.ISO_DATE), Objects.isNull(dataFim.getValue()) ? null : dataFim.getValue().format(DateTimeFormatter.ISO_DATE));
        atualizarSomatorioLabel(somatorio.toString());
    }

    private void atualizarSomatorioLabel(String valorString) {
        Platform.runLater(() -> {
            try {
                somatorioValorItem.setText("R$ " + configurarSomatorio(valorString));
            } catch (Exception e) {
                somatorioValorItem.setText(ERROR_MSG);
                System.err.println("Erro ao converter resposta: " + e.getMessage());
            }
        });
    }


    private String configurarSomatorio(String valorString) {
        BigDecimal somatorio = new BigDecimal(valorString);
        NumberFormat formatoMoeda = NumberFormat.getInstance(new Locale("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        return formatoMoeda.format(somatorio);
    }


    private String formatarSomatorio(String valorString) {
        Pattern pattern = Pattern.compile("(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{1,2})?)");
        Matcher matcher = pattern.matcher(valorString);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Nenhum número válido encontrado na resposta.");
        }
        String valorLimpo = matcher.group().replace(",", ".");
        BigDecimal somatorio = new BigDecimal(valorLimpo);
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
}