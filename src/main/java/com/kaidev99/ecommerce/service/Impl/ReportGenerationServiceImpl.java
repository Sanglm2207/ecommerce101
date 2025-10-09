package com.kaidev99.ecommerce.service.Impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kaidev99.ecommerce.dto.ReportRequestDTO;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportGenerationServiceImpl  implements ReportGenerationService {

    private final ProductRepository productRepository;

    private static final String FONT_PATH = "fonts/Roboto-Regular.ttf";
    private static final String FONT_BOLD_PATH = "fonts/Roboto-Bold.ttf";

    @Override
    public ByteArrayOutputStream generatePdfReport(ReportRequestDTO requestDTO) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // --- TẢI FONT TIẾNG VIỆT ---
        PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfFont fontBold = PdfFontFactory.createFont(FONT_BOLD_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        document.setFont(font);

        // --- THÊM HEADER (QUỐC HIỆU, TIÊU NGỮ) ---
        addHeader(document, fontBold);

        // --- THÊM TIÊU ĐỀ BÁO CÁO ---
        addTitle(document, fontBold, requestDTO);

        // --- THÊM NỘI DUNG BÁO CÁO DỰA TRÊN LOẠI ---
        switch (requestDTO.reportType()) {
            case INVENTORY:
                generateInventoryReport(document, font, fontBold);
                break;
            case SALES:
                generateSalesReport(document, font, fontBold, requestDTO);
                break;
            case TOP_PRODUCTS:
                generateTopProductsReport(document, font, fontBold, requestDTO);
                break;
        }

        document.close();
        return baos;
    }

    @Override
    public void addHeader(Document document, PdfFont fontBold) {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // CỘT TRÁI: Tên cửa hàng
        Paragraph shopInfo = new Paragraph("KAIDEV SHOP\nĐịa chỉ: 123 Đường ABC, TP.HCM\n---")
                .setTextAlignment(TextAlignment.CENTER).setFont(fontBold);
        headerTable.addCell(new Cell().add(shopInfo).setBorder(null));

        // CỘT PHẢI: Quốc hiệu, Tiêu ngữ
        Paragraph nationalAnthem = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n---")
                .setTextAlignment(TextAlignment.CENTER).setFont(fontBold);
        headerTable.addCell(new Cell().add(nationalAnthem).setBorder(null));

        document.add(headerTable);
    }

    @Override
    public void addTitle(Document document, PdfFont fontBold, ReportRequestDTO requestDTO) {
        String titleString = "BÁO CÁO ";
        switch (requestDTO.reportType()) {
            case INVENTORY: titleString += "TỒN KHO"; break;
            case SALES: titleString += "DOANH THU"; break;
            case TOP_PRODUCTS: titleString += "SẢN PHẨM BÁN CHẠY"; break;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateRange = String.format("(Từ ngày %s đến ngày %s)",
                requestDTO.startDate().format(formatter),
                requestDTO.endDate().format(formatter));

        Paragraph title = new Paragraph(titleString)
                .setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontSize(20).setMarginTop(20);
        Paragraph subtitle = new Paragraph(dateRange)
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setMarginBottom(20);

        document.add(title);

        document.add(subtitle);
    }

    @Override
    public void generateInventoryReport(Document document, PdfFont font, PdfFont fontBold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Header của bảng
        table.addHeaderCell(new Cell().add(new Paragraph("STT")).setFont(fontBold));
        table.addHeaderCell(new Cell().add(new Paragraph("Tên Sản phẩm")).setFont(fontBold));
        table.addHeaderCell(new Cell().add(new Paragraph("Danh mục")).setFont(fontBold));
        table.addHeaderCell(new Cell().add(new Paragraph("Số lượng còn lại")).setFont(fontBold).setTextAlignment(TextAlignment.RIGHT));

        // Lấy dữ liệu
        List<Product> products = productRepository.findAll();
        int count = 1;
        for (Product product : products) {
            table.addCell(String.valueOf(count++));
            table.addCell(product.getName());
            table.addCell(product.getCategory().getName());
            table.addCell(new Cell().add(new Paragraph(String.valueOf(product.getStockQuantity()))).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(table);
    }

    @Override
    public void generateSalesReport(Document document, PdfFont font, PdfFont fontBold, ReportRequestDTO requestDTO) {
        // TODO: Lấy dữ liệu đơn hàng trong khoảng thời gian từ OrderRepository
        // Tạo bảng báo cáo doanh thu: Mã ĐH, Ngày đặt, Khách hàng, Tổng tiền
        document.add(new Paragraph("Báo cáo Doanh thu - Tính năng đang phát triển..."));
    }

    @Override
    public void generateTopProductsReport(Document document, PdfFont font, PdfFont fontBold, ReportRequestDTO requestDTO) {
        // TODO: Viết query trong OrderItemRepository để tính tổng số lượng bán ra của mỗi sản phẩm
        // trong khoảng thời gian. Sắp xếp giảm dần.
        // Tạo bảng báo cáo: STT, Tên sản phẩm, Số lượng đã bán
        document.add(new Paragraph("Báo cáo Sản phẩm bán chạy - Tính năng đang phát triển..."));
    }
}
