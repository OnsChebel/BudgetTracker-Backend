package com.ruse.budgettracker.service;

import com.ruse.budgettracker.model.Budget;
import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.repository.BudgetRepository;
import com.ruse.budgettracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public Budget createBudget(Budget budget) {
        budget.setId(null);
        validateAndLink(budget);
        return budgetRepository.save(budget);
    }

    public List<Budget> findByUserId(Long id) {
        return budgetRepository.findByUserId(id);
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such budget found"));
    }

    public Budget updateBudget(Budget budget) {
        validateAndLink(budget);
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    private void validateAndLink(Budget budget) {
        if (budget.getLimitAmount() == null || budget.getLimitAmount() <= 0) {
            throw bad("Budget limit must be greater than zero");
        }
        if (budget.getPeriodMonth() < 1 || budget.getPeriodMonth() > 12) {
            throw bad("Month must be between 1 and 12");
        }
        if (budget.getPeriodYear() < 2000 || budget.getPeriodYear() > 2100) {
            throw bad("Year must be between 2000 and 2100");
        }
        if (budget.getCategory() == null || budget.getCategory().getId() == null) {
            throw bad("Category is required");
        }

        Category category = categoryRepository.findById(budget.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (!category.getUser().getId().equals(budget.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This category does not belong to you");
        }
        budget.setCategory(category);
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}