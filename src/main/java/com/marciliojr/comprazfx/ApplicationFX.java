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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.marciliojr.comprazfx.infra.ComprazUtils.parseDate;

public class ApplicationFX extends Application {
    private static final Logger logger = LogManager.getLogger(ApplicationFX.class);
    private static final String APP_TITLE = "ComprazFx - Gestor compras por cupom fiscal";
    private static final String ERROR_MSG = "Erro ao carregar total";
    private static final int SPLASH_SCREEN_WIDTH = 800;
    private static final int SPLASH_SCREEN_HEIGHT = 600;
    private static final int SPLASH_SCREEN_DISPLAY_TIME = 3000;
    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();
    private final ObservableList<CompraDTO> listaCompras = FXCollections.observableArrayList();
    private final ObservableList<ItemDTO> listaProdutos = FXCollections.observableArrayList();
    private final ItemService itemService = SpringBootApp.context.getBean(ItemService.class);
    private final PDFDadosService pdfDadosService = SpringBootApp.context.getBean(PDFDadosService.class);
    private final PDFGeradorItensCupom pdfGeradorItensCupom = SpringBootApp.context.getBean(PDFGeradorItensCupom.class);
    private final PDFGeradorProdutos pdfGeradorProdutos = SpringBootApp.context.getBean(PDFGeradorProdutos.class);
    private final CompraService compraService = SpringBootApp.context.getBean(CompraService.class);
    private final EstabelecimentoService estabelecimentoService = SpringBootApp.context.getBean(EstabelecimentoService.class);
    private Scene scene;
    private String currentUserCSS;
    @FXML
    private Button carregarPdfButton;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private TextField nomeEstabelecimentoCadastro;
    @FXML
    private TextField nomeEstabelecimentoPesquisaItens;
    @FXML
    private TextField nomeEstabelecimentoPesquisaCupons;
    @FXML
    private TextField nomeProdutoTextField;
    @FXML
    private TableView<ItemDTO> tabelaItens;
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
    private DatePicker dataInicioItens;
    @FXML
    private DatePicker dataFimItens;
    @FXML
    private DatePicker dataInicioCupons;
    @FXML
    private DatePicker dataFimCupons;
    @FXML
    private DatePicker dataInicioProdutos;
    @FXML
    private DatePicker dataFimProdutos;
    @FXML
    private Label somatorioValorItem;
    @FXML
    private DatePicker dataInicioCompras;
    @FXML
    private DatePicker dataFimCompras;
    @FXML
    private DatePicker dataInicioProduto;
    @FXML
    private DatePicker dataFimProduto;
    @FXML
    private ComboBox<String> comboBoxCss;
    @FXML
    private ComboBox<TipoCupom> comboTipoCupom;
    @FXML
    private Button buttonAplicar;
    @FXML
    private Tab abaItensCupons;
    @FXML
    private Tab abaCadastro;
    @FXML
    private Tab abaCupons;
    @FXML
    private ComboBox<TipoCupom> tipoCupomComboItens;
    @FXML
    private ComboBox<TipoCupom> tipoCupomComboCupons;
    @FXML
    private DatePicker dataCadastro;
    @FXML
    private ComboBox<TipoCupom> comboCupom;
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
        configuraTabelaAbaItens();
        configuraTabelaAbaCompras();
        configuraTabelaAbaProdutos();
        tabelaItens.setItems(listaItens);
        tabelaCompras.setItems(listaCompras);
        tabelaProduto.setItems(listaProdutos);
        List<String> nomesEstabelecimentos = estabelecimentoService.listarTodos().stream().map(Estabelecimento::getNomeEstabelecimento).collect(Collectors.toList());
        TextFields.bindAutoCompletion(nomeEstabelecimentoCadastro, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaItens, nomesEstabelecimentos);
        TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaCupons, nomesEstabelecimentos);
        tabelaCompras.setRowFactory(tv -> {
            TableRow<CompraDTO> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Deletar Cupom");
            deleteItem.setOnAction(event -> excluirCompra(row.getItem()));
            contextMenu.getItems().addAll(deleteItem);
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
        abaItensCupons.setOnSelectionChanged(event -> {
            if (abaItensCupons.isSelected()) {
                List<String> estabelecimentoAtualizados = estabelecimentoService.listarTodos().stream().map(Estabelecimento::getNomeEstabelecimento).collect(Collectors.toList());
                TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaItens, estabelecimentoAtualizados);
            }
        });
        abaCadastro.setOnSelectionChanged(event -> {
            if (abaCadastro.isSelected()) {
                List<String> estabelecimentoAtualizados = estabelecimentoService.listarTodos().stream().map(Estabelecimento::getNomeEstabelecimento).collect(Collectors.toList());
                TextFields.bindAutoCompletion(nomeEstabelecimentoCadastro, estabelecimentoAtualizados);
            }
        });
        abaCupons.setOnSelectionChanged(event -> {
            if (abaCupons.isSelected()) {
                List<String> estabelecimentoAtualizados = estabelecimentoService.listarTodos().stream().map(Estabelecimento::getNomeEstabelecimento).collect(Collectors.toList());
                TextFields.bindAutoCompletion(nomeEstabelecimentoPesquisaCupons, estabelecimentoAtualizados);
            }
        });
        tipoCupomComboItens.setItems(FXCollections.observableArrayList(TipoCupom.values()));
        tipoCupomComboCupons.setItems(FXCollections.observableArrayList(TipoCupom.values()));
        tipoCupomComboProdutos.setItems(FXCollections.observableArrayList(TipoCupom.values()));
    }

    private void carregarComboTipoCupom() {
        comboTipoCupom.getItems().clear();
        comboTipoCupom.getItems().addAll(Arrays.asList(TipoCupom.values()));
    }

    @FXML
    public void aplicarEstilo() {
        String cssSelecionado = comboBoxCss.getValue();
        if (cssSelecionado == null || cssSelecionado.isEmpty()) {
            return;
        }
        Scene cenaAtual = comboBoxCss.getScene();
        if (cenaAtual == null) {
            return;
        }
        if (currentUserCSS != null) {
            cenaAtual.getStylesheets().remove(currentUserCSS);
        }
        String novoCSS = getClass().getResource("/css/" + cssSelecionado).toExternalForm();
        cenaAtual.getStylesheets().add(novoCSS);
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

    public void setScene(Scene scene) {
        this.scene = scene;
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

    private void configuraTabelaAbaItens() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colunaEstabelecimento.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompraFormatada"));
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
        pdfDadosService.processarDadosEPersistir(pdfString, nomeEstabelecimentoCadastro.getText(), parseDate(dataCadastro.getValue().toString()), comboTipoCupom.getValue());
    }

    @FXML
    public void gerarPDF(ActionEvent event) throws IOException {
        BigDecimal somatorio = getSomatorio();
        byte[] pdfBytes = pdfGeradorItensCupom.generatePDF(new ArrayList<>(listaItens), somatorio.toString());
        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
        montarRelatorioPDF(pdfBytes, "itens");
    }

    @FXML
    public void gerarPDFProduto(ActionEvent event) throws IOException {
        byte[] pdfBytes = pdfGeradorProdutos.generatePDF(new ArrayList<>(listaProdutos));
        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
        montarRelatorioPDF(pdfBytes, "produtos");
    }

    private BigDecimal getSomatorio() {
        String nomeEstabelecimento = getNome(nomeEstabelecimentoPesquisaItens);
        TipoCupom tipoCupom = tipoCupomComboItens.getValue();
        String dataInicioFormatada = parseDateToString(dataInicioItens);
        String dataFimFormatada = parseDateToString(dataFimItens);
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
        TipoCupom tipoCupom = tipoCupomComboProdutos.getValue();
        String dataInicio = dataInicioProdutos.getValue() != null ? dataInicioProdutos.getValue().toString() : null;
        String dataFim = dataFimProdutos.getValue() != null ? dataFimProdutos.getValue().toString() : null;

        listaProdutos.clear();
        listaProdutos.addAll(itemService.listarItensPorNomeEPeriodo(nomeProduto, tipoCupom, dataInicio, dataFim));
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
        TipoCupom tipoCupom = tipoCupomComboCupons.getValue();
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
        String nomeEstabelecimento = getNome(nomeEstabelecimentoPesquisaItens);
        TipoCupom tipoCupom = tipoCupomComboItens.getValue();
        String dataInicioFormatada = parseDateToString(dataInicioItens);
        String dataFimFormatada = parseDateToString(dataFimItens);
        return itemService.listarItensPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicioFormatada, dataFimFormatada);
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

    @FXML
    private void atualizarTotalItens() {
        String nomeEstabelecimento = nomeEstabelecimentoPesquisaItens.getText();
        TipoCupom tipoCupom = tipoCupomComboItens.getValue();
        String dataInicio = dataInicioItens.getValue() != null ? dataInicioItens.getValue().toString() : null;
        String dataFim = dataFimItens.getValue() != null ? dataFimItens.getValue().toString() : null;

        BigDecimal total = itemService.somarValorUnitarioPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicio, dataFim);
        somatorioValorItem.setText(NumberFormat.getCurrencyInstance().format(total));
    }

    @FXML
    private void pesquisarItens(ActionEvent event) {
        String nomeEstabelecimento = nomeEstabelecimentoPesquisaItens.getText();
        TipoCupom tipoCupom = tipoCupomComboItens.getValue();
        String dataInicio = dataInicioItens.getValue() != null ? dataInicioItens.getValue().toString() : null;
        String dataFim = dataFimItens.getValue() != null ? dataFimItens.getValue().toString() : null;

        listaItens.clear();
        listaItens.addAll(itemService.listarItensPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicio, dataFim));
        atualizarTotalItens();
    }

    @FXML
    private void pesquisarCupons(ActionEvent event) {
        String nomeEstabelecimento = nomeEstabelecimentoPesquisaCupons.getText();
        TipoCupom tipoCupom = tipoCupomComboCupons.getValue();
        String dataInicio = dataInicioCupons.getValue() != null ? dataInicioCupons.getValue().toString() : null;
        String dataFim = dataFimCupons.getValue() != null ? dataFimCupons.getValue().toString() : null;

        listaCompras.clear();
        listaCompras.addAll(compraService.listarComprasPorEstabelecimentoEPeriodo(nomeEstabelecimento, tipoCupom, dataInicio, dataFim));
    }
}
