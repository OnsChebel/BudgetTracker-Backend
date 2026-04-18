package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.CategoryService;
import com.ruse.budgettracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping("/new-category")
    public Category addCategory(@RequestBody Category category) {
        User currentUser = userService.getLoggedInUser();
        category.setUser(currentUser);
        return categoryService.createCategory(category);
    }

    @GetMapping("/my-categories")
    public List<Category> getMyCategories() {
        User currentUser = userService.getLoggedInUser();
        return categoryService.findByUserId(currentUser.getId());
    }

    @GetMapping("/category/{id}")
    public Category getCategoryById(@PathVariable ("id") Long id) {
        User currentUser = userService.getLoggedInUser();
        Category category = categoryService.getCategoryById(id);

        if(!category.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this category");
        }
        return category;
    }

    @PutMapping("/update-category")
    public Category updateCategory(@RequestBody Category category) {
        User currentUser = userService.getLoggedInUser();
        Category existingCategory = categoryService.getCategoryById(category.getId());

        if(!existingCategory.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this category");
        }
        category.setUser(currentUser);
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/delete-category/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable ("id") Long id) {
       User currentUser = userService.getLoggedInUser();
       Category category = categoryService.getCategoryById(id);

       if(!category.getUser().getId().equals(currentUser.getId())){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this budget");
       }
       categoryService.deleteCategoryById(id);
    }


}