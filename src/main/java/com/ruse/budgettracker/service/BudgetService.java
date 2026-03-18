package com.ruse.budgettracker.service;


import com.ruse.budgettracker.model.Budget;
import com.ruse.budgettracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {
    private BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget createBudget(Budget budget){
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets(){
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(Long id){
        return budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("No such budget found"));
    }

    public Budget updateBudget(Budget budget){
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id){
        budgetRepository.deleteById(id);
    }
}
