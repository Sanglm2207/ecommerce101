package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.*;
import com.kaidev99.ecommerce.entity.Category;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.mapper.ProductMapper;
import com.kaidev99.ecommerce.repository.CategoryRepository;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.CategoryService;
import com.kaidev99.ecommerce.service.EventPublisher;
import com.kaidev99.ecommerce.service.ProductService;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final EventPublisher eventPublisher;

    @Override
    public Page<ProductSummaryDTO> findAll(Specification<Product> spec, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // Dùng map của Page để chuyển đổi từng Product thành ProductSummaryDTO
        return productPage.map(productMapper::toProductSummaryDTO);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Category category = categoryService.getCategoryById(productRequestDTO.categoryId());

        Product product = new Product();
        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setStockQuantity(productRequestDTO.stockQuantity());
        product.setCategory(category);

        product.setThumbnailUrl(productRequestDTO.thumbnailUrl());
        product.setImageUrls(productRequestDTO.imageUrls());

        Product savedProduct = productRepository.save(product);

        // --- GỬI SỰ KIỆN THÔNG BÁO SẢN PHẨM MỚI ---
        NotificationPayload payload = NotificationPayload.builder()
                .type("NEW_PRODUCT")
                .message("Sản phẩm mới vừa được thêm: " + savedProduct.getName())
                .link("/admin/products/edit/" + savedProduct.getId())
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publishNotification("notification.admin", payload);
        // ---------------------------------------------

        return savedProduct;
    }

    @Override
    public Page<ProductSummaryDTO> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductSummaryDTO> dtos = productPage.getContent().stream()
                .map(productMapper::toProductSummaryDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productPage.getTotalElements());
    }

    @Override
    public List<Product> getFeaturedProducts(int limit) {
        return productRepository.findByIsFeaturedTrueOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    public ProductDetailDTO getProductDetailById(Long id) {
        Product product = this.getProductById(id); // Tận dụng lại phương thức đã có
        List<Product> relatedProducts = productRepository.findTop4ByCategoryIdAndIdNot(
                product.getCategory().getId(),
                id
        );
        return new ProductDetailDTO(product, relatedProducts);
    }

    @Override
    public List<ProductSuggestionDTO> getSearchSuggestions(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findSuggestions(keyword, PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryService.getCategoryById(productRequestDTO.categoryId());

        existingProduct.setName(productRequestDTO.name());
        existingProduct.setDescription(productRequestDTO.description());
        existingProduct.setPrice(productRequestDTO.price());
        existingProduct.setStockQuantity(productRequestDTO.stockQuantity());
        existingProduct.setCategory(category);

        existingProduct.setThumbnailUrl(productRequestDTO.thumbnailUrl());
        existingProduct.setImageUrls(productRequestDTO.imageUrls());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductImportResult importProducts(MultipartFile file) throws IOException, CsvValidationException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name is null");
        }

        if (filename.endsWith(".xlsx")) {
            return importFromExcel(file);
        } else if (filename.endsWith(".csv")) {
            return importFromCsv(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type. Please upload an Excel (.xlsx) or CSV file.");
        }
    }

    private ProductImportResult importFromExcel(MultipartFile file) throws IOException {
        ProductImportResult result = new ProductImportResult();
        List<Product> productsToSave = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua dòng header
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            int rowNumber = 1;
            while (rowIterator.hasNext()) {
                rowNumber++;
                Row row = rowIterator.next();
                try {
                    Product product = parseProductFromRow(row);
                    productsToSave.add(product);
                    result.incrementSuccessCount();
                } catch (Exception e) {
                    result.addError(rowNumber, e.getMessage());
                }
            }
        }

        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
        }
        return result;
    }

    private ProductImportResult importFromCsv(MultipartFile file) throws IOException, CsvValidationException {
        ProductImportResult result = new ProductImportResult();
        List<Product> productsToSave = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            // Bỏ qua dòng header
            csvReader.readNext();

            String[] line;
            int rowNumber = 1;
            while ((line = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    Product product = parseProductFromCsvLine(line);
                    productsToSave.add(product);
                    result.incrementSuccessCount();
                } catch (Exception e) {
                    result.addError(rowNumber, e.getMessage());
                }
            }
        }

        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
        }
        return result;
    }

    private Product parseProductFromRow(Row row) {
        String name = getCellStringValue(row.getCell(0));
        String categoryName = getCellStringValue(row.getCell(1));
        BigDecimal price = new BigDecimal(getCellStringValue(row.getCell(2)));
        int stockQuantity = (int) Double.parseDouble(getCellStringValue(row.getCell(3)));
        String description = getCellStringValue(row.getCell(4));

        return createAndValidateProduct(name, categoryName, price, stockQuantity, description);
    }

    private Product parseProductFromCsvLine(String[] line) {
        String name = line[0];
        String categoryName = line[1];
        BigDecimal price = new BigDecimal(line[2]);
        int stockQuantity = Integer.parseInt(line[3]);
        String description = line.length > 4 ? line[4] : "";

        return createAndValidateProduct(name, categoryName, price, stockQuantity, description);
    }

    private Product createAndValidateProduct(String name, String categoryName, BigDecimal price, int stockQuantity, String description) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Product name is required.");
        if (categoryName == null || categoryName.isEmpty()) throw new IllegalArgumentException("Category name is required.");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price must be a non-negative number.");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity cannot be negative.");

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Category '" + categoryName + "' not found."));

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setDescription(description);
        // Có thể set các giá trị mặc định cho ảnh ở đây

        return product;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
