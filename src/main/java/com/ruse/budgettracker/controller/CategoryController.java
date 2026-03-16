package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/newcategory")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category newCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @GetMapping("/allcategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable ("id") Long id) {
        Category category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PutMapping("/updatecategory")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable ("id") Long id) {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}