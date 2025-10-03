package com.kaidev99.ecommerce.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kaidev99.ecommerce.dto.CategoryDTO;
import com.kaidev99.ecommerce.entity.Category;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.CategoryRepository;
import com.kaidev99.ecommerce.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.name()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.name() + "' already exists.");
        }
        Category category = new Category();
        category.setName(categoryDTO.name());
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = getCategoryById(id);
        category.setName(categoryDTO.name());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
