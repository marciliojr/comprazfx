package com.marciliojr.comprazfx;

import com.marciliojr.comprazfx.model.dto.ItemDTO;
import com.marciliojr.comprazfx.service.ItemService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EditarItemController {

    @FXML
    private TextField nomeTextField;

    @FXML
    private TextField quantidadeTextField;

    @FXML
    private TextField valorUnitarioTextField;

    @FXML
    private TextField valorTotalTextField;

    @Autowired
    private ItemService itemService;

    private ItemDTO item;
    private Runnable callback;

    public void setItem(ItemDTO item) {
        this.item = item;
        nomeTextField.setText(item.getNome());
        quantidadeTextField.setText(item.getQuantidade().toString());
        valorUnitarioTextField.setText(item.getValorUnitario().toString());
        valorTotalTextField.setText(item.getValorTotal().toString());
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @FXML
    private void salvar() {
        try {
            item.setNome(nomeTextField.getText());
            item.setQuantidade(new BigDecimal(quantidadeTextField.getText()));
            item.setValorUnitario(new BigDecimal(valorUnitarioTextField.getText()));
            item.setValorTotal(new BigDecimal(valorTotalTextField.getText()));

            itemService.atualizarItem(item);
            if (callback != null) {
                callback.run();
            }
            fecharJanela();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO");
            alert.setHeaderText(null);
            alert.setContentText("Perdão Houve um erro no salvamento do item.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) nomeTextField.getScene().getWindow();
        stage.close();
    }
} 