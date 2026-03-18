package com.ruse.budgettracker.controller;


import com.ruse.budgettracker.model.Budget;
import com.ruse.budgettracker.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/newbudget")
    public ResponseEntity<Budget> budget(@RequestBody Budget budget){
        Budget newBudget = budgetService.createBudget(budget);
        return new ResponseEntity<>(newBudget, HttpStatus.CREATED);
    }

    @GetMapping("/allbudgets")
    public ResponseEntity<List<Budget>> getAllBudgets(){
        return new ResponseEntity<>(budgetService.getAllBudgets(), HttpStatus.OK);
    }

    @GetMapping("/budget/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable ("id") Long id){
        return new ResponseEntity<>(budgetService.getBudgetById(id), HttpStatus.OK);
    }

    @PutMapping("/updatebudget")
    public ResponseEntity<Budget> updateBudget( @RequestBody Budget budget){
        return new ResponseEntity<>(budgetService.updateBudget(budget), HttpStatus.OK);
    }

    @DeleteMapping("/deletebudget/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable ("id") Long id){
        budgetService.deleteBudget(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
