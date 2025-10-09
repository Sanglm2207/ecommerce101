package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.ReportRequestDTO;
import com.kaidev99.ecommerce.service.ReportGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportGenerationService reportGenerationService;

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdfReport(@Valid @RequestBody ReportRequestDTO requestDTO) {
        try {
            ByteArrayOutputStream pdfStream = reportGenerationService.generatePdfReport(requestDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Báo cho trình duyệt tải file xuống với tên cụ thể
            String filename = String.format("BaoCao_%s_%s_den_%s.pdf",
                    requestDTO.reportType(),
                    requestDTO.startDate(),
                    requestDTO.endDate());
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());
        } catch (Exception e) {
            // Log lỗi ở đây
            return ResponseEntity.internalServerError().build();
        }
    }

}
