package com.marciliojr.comprazfx.infra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFDataProcessor {
    public static void processPDFData(String text) {
        // Regex para capturar Nome, Qtde, UN, e Valor Unitário
        String regex = "(.*?)\\s*\\(Código:\\s*\\d+\\)\\s*Qtde\\.:\\s*(\\d+[,.]?\\d*)\\s*UN:\\s*(\\w+)\\s*Vl\\. Unit\\.:\\s*(\\d+[,.]?\\d+)\\s*Vl\\. Total\\s*(\\d+[,.]?\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String nome = matcher.group(1).trim();
            String quantidade = matcher.group(2).trim();
            String unidade = matcher.group(3).trim();
            String valorUnitario = matcher.group(4).trim();
            String valorTotal = matcher.group(5).trim();

            System.out.printf("Produto: %s, Qtde: %s, UN: %s, Valor Unitário: %s%n",
                    nome, quantidade, unidade, valorUnitario);
        }
    }
}