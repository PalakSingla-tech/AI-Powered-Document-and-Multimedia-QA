package com.assignment.AI_Powered.Document.Multimedia.Q.A.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfUtilTest {
    private final PdfUtil pdfUtil = new PdfUtil();

    @Test
    void testExtractTest_Success() throws Exception
    {
        File tempFile = File.createTempFile("test", ".pdf");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.showText("Hello PDF World");
                contentStream.endText();
            }
            document.save(tempFile);
        }
        String result = pdfUtil.extractText(tempFile);
        assertTrue(result.contains("Hello PDF World"));
        tempFile.delete();
    }
}
