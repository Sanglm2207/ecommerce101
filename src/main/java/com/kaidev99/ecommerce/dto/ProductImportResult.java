package com.kaidev99.ecommerce.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductImportResult {
    private int successCount = 0;
    private int errorCount = 0;
    private List<String> errors = new ArrayList<>();

    public void addError(int rowNumber, String message) {
        this.errorCount++;
        this.errors.add("Row " + rowNumber + ": " + message);
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }
}
