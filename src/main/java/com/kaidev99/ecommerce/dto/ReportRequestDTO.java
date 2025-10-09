package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReportRequestDTO(
        @NotNull(message = "Report type is required") ReportType reportType,

        @NotNull(message = "Start date is required") LocalDate startDate,

        @NotNull(message = "End date is required") LocalDate endDate) {
    public enum ReportType {
        INVENTORY, // Báo cáo tồn kho
        SALES, // Báo cáo doanh thu
        TOP_PRODUCTS // Báo cáo sản phẩm bán chạy
    }
}