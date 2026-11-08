package com.ruse.budgettracker.service;

import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        category.setId(null);
        validate(category);
        return categoryRepository.save(category);
    }

    public List<Category> findByUserId(Long id) {
        return categoryRepository.findByUserId(id);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    public Category updateCategory(Category category) {
        validate(category);
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This category is used by transactions or budgets. Remove them first.");
        }
    }

    private void validate(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
        }
        category.setName(category.getName().trim());
    }
}