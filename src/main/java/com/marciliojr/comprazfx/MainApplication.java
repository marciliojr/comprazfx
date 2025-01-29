package com.marciliojr.comprazfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.marciliojr.comprazfx.infra.LocalDateAdapter;
import com.marciliojr.comprazfx.infra.MultipartFileUploader;
import com.marciliojr.comprazfx.model.ItemDTO;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
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

    private final ObservableList<ItemDTO> listaItens = FXCollections.observableArrayList();

    private File file;

    private static final String APP_TITLE = "Compraz - Gestor compras por nota fiscal";

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
                    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                    stage.setTitle(APP_TITLE);
                    stage.setScene(scene);
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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gerarUrlDosItens(nomeEstabelecimentoTextFieldPesquisa.getText(), dataInicio.getValue(), dataFim.getValue())))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::atualizarTabela)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Falha ao carregar os itens: " + ex.getMessage()));
                    return null;
                });
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
        return UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/item/itens")
                .queryParam("nomeEstabelecimento", nomeEstabelecimento)
                .queryParamIfPresent("dataInicio", java.util.Optional.ofNullable(dataInicio))
                .queryParamIfPresent("dataFim", java.util.Optional.ofNullable(dataFim))
                .toUriString();
    }

    private String gerarUrlSomatorioDias(String nomeEstabelecimento, LocalDate dataInicio, LocalDate dataFim) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/item/soma-valor-unitario")
                .queryParam("nomeEstabelecimento", nomeEstabelecimento)
                .queryParamIfPresent("dataInicio", java.util.Optional.ofNullable(dataInicio))
                .queryParamIfPresent("dataFim", java.util.Optional.ofNullable(dataFim))
                .toUriString();
    }

    private void atualizarTabela(String responseBody) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        try {
            Type itemListType = new TypeToken<List<ItemDTO>>() {}.getType();
            List<ItemDTO> itens = gson.fromJson(responseBody, itemListType);

            Platform.runLater(() -> {
                listaItens.clear();
                listaItens.addAll(itens);
            });

        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
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

    public static void main(String[] args) {
        launch();
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
            MultipartFileUploader.enviarArquivo(file, nomeEstabelecimentoTextField.getText().trim(), dataCadastro.getValue());
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
        alert.setContentText(MultipartFileUploader.teste());
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
        return UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/item/exportar/pdf")
                .queryParam("nomeEstabelecimento", nomeEstabelecimento)
                .queryParamIfPresent("dataInicio", java.util.Optional.ofNullable(dataInicio))
                .queryParamIfPresent("dataFim", java.util.Optional.ofNullable(dataFim))
                .toUriString();
    }

    @FXML
    public void gerarPDF(ActionEvent event) {
        String url = gerarUrlPDF(nomeEstabelecimentoTextFieldPesquisa.getText(), dataInicio.getValue(), dataFim.getValue());

        RestTemplate restTemplate = new RestTemplate();
        byte[] pdfBytes = restTemplate.getForObject(url, byte[].class);

        if (pdfBytes == null || pdfBytes.length == 0) {
            mostrarMensagem("Erro!", "O servidor não retornou um arquivo válido.");
            return;
        }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("relatorio_compras_"+ LocalDate.now().format(DateTimeFormatter.ISO_DATE) +".pdf");
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
