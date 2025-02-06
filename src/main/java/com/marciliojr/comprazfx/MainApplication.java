package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.infra.PersistenciaPDF;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainApplication extends Application {

    private static final String APP_TITLE = "Compraz - Gestor compras por nota fiscal";
    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();
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

    @FXML
    private void pesquisar(ActionEvent event) {
        carregarItens();
        carregarValorSomatorio();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Stage splashStage = createSplashScreen();
        splashStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                splashStage.close();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 900, 700);
                    stage.setTitle(APP_TITLE);
                    stage.setScene(scene);
                    stage.setOnCloseRequest((WindowEvent event) -> {
                        if (SpringBootApp.context != null) {
                            SpringBootApp.context.close(); // Finaliza o contexto do Spring
                        }
                        Platform.exit(); // Encerra o JavaFX
                        System.exit(0);  // Encerra a aplicação completamente
                    });
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    @FXML
    private void initialize() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colunaEstabelecimento.setCellValueFactory(new PropertyValueFactory<>("nomeEstabelecimento"));
        colunaDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));

        tabelaItens.setItems(listaItens);

    }

    private void carregarItens() {

    }


    private void carregarValorSomatorio() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gerarUrlSomatorioDias(nomeEstabelecimentoTextFieldPesquisa.getText(), dataInicio.getValue(), dataFim.getValue())))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::atualizarSomatorioLabel)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void atualizarSomatorioLabel(String responseBody) {
        Platform.runLater(() -> {
            try {
                // Expressão regular para encontrar o primeiro número decimal válido
                Pattern pattern = Pattern.compile("(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{1,2})?)");
                Matcher matcher = pattern.matcher(responseBody);

                if (!matcher.find()) {
                    somatorioValorItem.setText("Erro ao carregar total");
                    System.err.println("Nenhum número válido encontrado na resposta.");
                    return;
                }
                // Captura apenas o primeiro número válido
                String valorLimpo = matcher.group().replace(",", ".");

                // Converte para BigDecimal
                BigDecimal somatorio = new BigDecimal(valorLimpo);

                // Formata no padrão brasileiro (ex: 1.000,00)
                NumberFormat formatoMoeda = NumberFormat.getInstance(new Locale("pt", "BR"));
                formatoMoeda.setMinimumFractionDigits(2);
                String valorExibido = formatoMoeda.format(somatorio);

                somatorioValorItem.setText("R$ " + valorExibido);
            } catch (Exception e) {
                somatorioValorItem.setText("Erro ao carregar total");
                System.err.println("Erro ao converter resposta: " + e.getMessage());
            }
        });
    }


    private String gerarUrlDosItens(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }

    private String gerarUrlSomatorioDias(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }

    private void atualizarTabela(String responseBody) {
        try {
            List<ItemDTO> itens = null;

            Platform.runLater(() -> {
                listaItens.clear();
                listaItens.addAll(itens);
            });

        } catch (RuntimeException e) {
            System.err.println("Ao Listar " + e.getMessage());
        }
    }


    private Stage createSplashScreen() {
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);

        StackPane splashRoot = new StackPane();
        ImageView splashImage = new ImageView(new Image(getClass().getResource("/imagens/splash.png").toExternalForm()));
        splashImage.setFitWidth(700);
        splashImage.setFitHeight(500);
        splashRoot.getChildren().add(splashImage);
        Scene splashScene = new Scene(splashRoot, 700, 500);

        splashStage.setScene(splashScene);
        return splashStage;
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
            return;
        }

        try {
            PersistenciaPDF.enviarArquivo(file, nomeEstabelecimentoTextField.getText().trim(), dataCadastro.getValue());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Arquivo cadastrado com sucesso!");
            limparCampos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar", "Ocorreu um erro ao tentar cadastrar o arquivo: " + e.getMessage());
        }
    }

    @FXML
    private void teste(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Teste de Conexão");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private File abrirFileChooser(String descricao, String... extensoes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(descricao, extensoes));
        Stage stage = (Stage) carregarPdfButton.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void atualizarCampoTexto(TextField campo, String texto) {
        campo.setText(texto);
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

    private String gerarUrlPDF(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }

    @FXML
    public void gerarPDF(ActionEvent event) {
        byte[] pdfBytes = null;

        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
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

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
