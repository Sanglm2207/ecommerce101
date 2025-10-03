package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.CategoryDTO;
import com.kaidev99.ecommerce.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    List<Category> getAllCategories();

    Category getCategoryById(Long id);
}
