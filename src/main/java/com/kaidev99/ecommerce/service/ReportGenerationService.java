package com.kaidev99.ecommerce.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.kaidev99.ecommerce.dto.ReportRequestDTO;

public interface ReportGenerationService {
    ByteArrayOutputStream generatePdfReport(ReportRequestDTO requestDTO) throws IOException;

    void addHeader(Document document, PdfFont fontBold);

    void addTitle(Document document, PdfFont fontBold, ReportRequestDTO requestDTO);

    void generateInventoryReport(Document document, PdfFont font, PdfFont fontBold);

    void generateSalesReport(Document document, PdfFont font, PdfFont fontBold, ReportRequestDTO requestDTO);

    void generateTopProductsReport(Document document, PdfFont font, PdfFont fontBold, ReportRequestDTO requestDTO);
}
