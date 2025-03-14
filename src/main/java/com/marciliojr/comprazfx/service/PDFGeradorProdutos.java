package com.marciliojr.comprazfx.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PDFGeradorProdutos {

    public byte[] generatePDF(List<ItemDTO> items, String valorSomatorio) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);

            document.add(new Paragraph("Relatório de Compras")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .simulateBold()
                    .setMarginBottom(20));

            Table table = new Table(new float[]{4, 1.5f, 2.5f, 2.5f, 3});
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Nome do Item").simulateBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantidade").simulateBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));
            table.addHeaderCell(new Cell().add(new Paragraph("Vl.Unitário").simulateBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));
            table.addHeaderCell(new Cell().add(new Paragraph("Vl.Total").simulateBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));
            table.addHeaderCell(new Cell().add(new Paragraph("Estabelecimento").simulateBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));

            for (ItemDTO item : items) {
                table.addCell(new Cell().add(new Paragraph(item.getNome())));
                table.addCell(new Cell().add(new Paragraph(item.getQuantidade().toString())));
                table.addCell(new Cell().add(new Paragraph("R$ " + item.getValorUnitario())));
                table.addCell(new Cell().add(new Paragraph("R$ " + item.getValorTotal())));
                table.addCell(new Cell().add(new Paragraph(item.getNomeEstabelecimento())));
            }

            document.add(table);

            Paragraph total = new Paragraph("Valor Total das Compras: R$ " + valorSomatorio)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .simulateBold()
                    .setFontSize(12)
                    .setFontColor(ColorConstants.RED)
                    .setMarginTop(20);

            document.add(total);

            Paragraph date = new Paragraph("Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .simulateItalic();

            document.add(date);

            document.close();
            return outputStream.toByteArray();
        }
    }

}
