package com.marciliojr.comprazfx.infra;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFExtractor {

    public String extrairTextoPDF(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            System.err.println("Erro: Arquivo PDF não encontrado ou inválido.");
            return null;
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            String text = pdfStripper.getText(document);

            return text;
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo PDF: " + e.getMessage());
        }

        return null;
    }
}
