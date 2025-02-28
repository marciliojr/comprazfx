package com.marciliojr.comprazfx.service;

import com.marciliojr.comprazfx.model.dto.ItemDTO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtratorDeItensService {

    public static void main(String[] args) {


        String url = "F://Projetos//Java//comprazfx//nota.jpg";

        File f = new File(url);
        try {
            File file = ImagePreProcessor.preprocessImage(f);
            ExtratorDeItensService extratorDeItensService = new ExtratorDeItensService();
            List<ItemDTO> itemDTOS = extratorDeItensService.extrairItensDaImagem(file);
            System.out.println(itemDTOS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ItemDTO> extrairItensDaImagem(File imagem) {
        List<ItemDTO> itens = new ArrayList<>();

        try {
            // Inicializa o Tesseract OCR
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("por");
            tesseract.setTessVariable("user_defined_dpi", "300");
            tesseract.setPageSegMode(6);

            BufferedImage bufferedImage = ImageIO.read(imagem);
            String textoExtraido = tesseract.doOCR(bufferedImage);

            // Expressão regular para extrair os itens
            String regex = "(\\d{3})\\s+\\d{13}\\s+(.+?)\\s+(\\d+)\\s+(\\d+,\\d{2})\\s+(\\d+,\\d{2})";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textoExtraido);

            while (matcher.find()) {
                String item = matcher.group(1);
                String descricao = matcher.group(2).trim();
                int quantidade = Integer.parseInt(matcher.group(3));
                String valorUnitario = matcher.group(4);
                String valorItem = matcher.group(5);
                ItemDTO dto = new ItemDTO(descricao, BigDecimal.valueOf(quantidade), BigDecimal.valueOf(Long.valueOf(valorUnitario)), BigDecimal.valueOf(Long.valueOf(valorItem)));

                itens.add(dto);
            }

        } catch (TesseractException | java.io.IOException e) {
            System.err.println("Erro ao processar a imagem: " + e.getMessage());
        }

        return itens;
    }
}

