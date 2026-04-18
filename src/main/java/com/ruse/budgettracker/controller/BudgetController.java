package com.ruse.budgettracker.controller;


import com.ruse.budgettracker.model.Budget;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.BudgetService;
import com.ruse.budgettracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final UserService userService;

    public BudgetController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    @PostMapping("/new-budget")
    public Budget createBudget(@RequestBody Budget budget){
        User currentUser = userService.getLoggedInUser();
        budget.setUser(currentUser);
        return budgetService.createBudget(budget);
    }

    @GetMapping("/my-budgets")
    public List<Budget> getMyBudgets(){
        User currentUser = userService.getLoggedInUser();
        return budgetService.findByUserId(currentUser.getId());
    }

    @GetMapping("/budget/{id}")
    public Budget getBudgetById(@PathVariable ("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Budget budget = budgetService.getBudgetById(id);

        if(!budget.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this budget");
        }
        return budget;
    }

    @PutMapping("/update-budget")
    public Budget updateBudget( @RequestBody Budget budget){
        User currentUser = userService.getLoggedInUser();
        Budget existingBudget = budgetService.getBudgetById(budget.getId());

        if(!existingBudget.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this budget");
        }
        budget.setUser(currentUser);
        return budgetService.updateBudget(budget);
    }

    @DeleteMapping("/delete-budget/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@PathVariable ("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Budget budget = budgetService.getBudgetById(id);

        if(!budget.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this budget");
        }

        budgetService.deleteBudget(id);
    }
}
