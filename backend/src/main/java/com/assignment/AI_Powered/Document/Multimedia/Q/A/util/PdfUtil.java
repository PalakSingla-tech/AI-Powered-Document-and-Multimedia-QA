package com.assignment.AI_Powered.Document.Multimedia.Q.A.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfUtil {
    public String extractText(File file) throws IOException {
        try(PDDocument document = Loader.loadPDF(file)){
            return new PDFTextStripper().getText(document);
        }
    }
}
