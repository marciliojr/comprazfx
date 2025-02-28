package com.marciliojr.comprazfx.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.UnitValue;
import com.marciliojr.comprazfx.model.dto.ItemDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDFGeradorItensCupom {

    public byte[] generatePDF(List<ItemDTO> items, String valorSomatorio) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);

            document.add(new Paragraph("Relatório de Compras").setFontSize(14).setFontColor(ColorConstants.BLACK).simulateBold());

            float[] columnWidths = {3, 2, 2, 3, 2, 2};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Nome do Item")).simulateBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Quantidade")).simulateBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Unidade")).simulateBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Valor Unitário")).simulateBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Valor Total")).simulateBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Estabelecimento")).simulateBold());

            for (ItemDTO item : items) {
                table.addCell(new Cell().add(new Paragraph(item.getNome())));
                table.addCell(new Cell().add(new Paragraph(item.getQuantidade().toString())));
                table.addCell(new Cell().add(new Paragraph(item.getUnidade())));
                table.addCell(new Cell().add(new Paragraph("R$ " + item.getValorUnitario())));
                table.addCell(new Cell().add(new Paragraph("R$ " + item.getValorTotal())));
                table.addCell(new Cell().add(new Paragraph(item.getNomeEstabelecimento())));
            }

            document.add(table);

            Text palavra = new Text("Valor total das compras no periodo: ").simulateBold();
            Text palavra2 = new Text(valorSomatorio).addStyle(new Style().setFontColor(ColorConstants.RED).simulateBold().setFontSize(12));
            Paragraph valorTotal = new Paragraph().add(palavra).add(palavra2);

            document.add(valorTotal);
            document.close();

            return outputStream.toByteArray();
        }
    }
}