package com.marciliojr.comprazfx.service;

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
import java.text.DecimalFormat;
import java.util.List;

@Service
public class PDFGeradorProdutos {

    public byte[] generatePDF(List<ItemDTO> items) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);

            Paragraph header = new Paragraph("Relatorio de produtos")
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            Paragraph dashedLine = new Paragraph("------------------------------------------------")
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(dashedLine);

            float[] columnWidths = {4, 1, 2, 2};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Produto")).setBorder(null));
            table.addHeaderCell(new Cell().add(new Paragraph("Qtd")).setBorder(null).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Valor Unit.")).setBorder(null).setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Cell().add(new Paragraph("Total")).setBorder(null).setTextAlignment(TextAlignment.RIGHT));

            double subtotal = 0.0;
            DecimalFormat df = new DecimalFormat("0.00");

            for (ItemDTO item : items) {
                table.addCell(new Cell().add(new Paragraph(item.getNome())).setBorder(null));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantidade())))
                        .setBorder(null)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph("R$ " + df.format(item.getValorUnitario())))
                        .setBorder(null)
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph("R$ " + df.format(item.getValorTotal())))
                        .setBorder(null)
                        .setTextAlignment(TextAlignment.RIGHT));
                subtotal += item.getValorTotal().doubleValue();
            }
            document.add(table);

            document.add(new Paragraph("------------------------------------------------").setTextAlignment(TextAlignment.CENTER));

            Paragraph subtotalParagraph = new Paragraph(String.format("Subtotal:%34s", "R$ " + df.format(subtotal)));
            document.add(subtotalParagraph);

            Paragraph thankYou = new Paragraph("     ComprazFX!")
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(thankYou);

            document.close();
            return outputStream.toByteArray();
        }
    }
}
