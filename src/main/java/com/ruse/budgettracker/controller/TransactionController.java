package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.model.Transaction;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.TransactionService;
import com.ruse.budgettracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/new-transaction")
    public Transaction createTransaction(@RequestBody Transaction transaction){
        User currentUser = userService.getLoggedInUser();
        transaction.setUser(currentUser);
        return transactionService.createTransaction(transaction);
    }

    @GetMapping("/my-transactions")
    public List<Transaction> getMyTransactions(){
        User currentUser = userService.getLoggedInUser();
        return transactionService.findByUserId(currentUser.getId());
    }

    @GetMapping("/transaction/{id}")
    public Transaction getTransactionById(@PathVariable ("id") Long id) {
        User currentUser = userService.getLoggedInUser();
        Transaction transaction = transactionService.getTransactionById(id);
        if(!transaction.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to see this transaction");
        }
        return transactionService.getTransactionById(id);
    }

    @PutMapping("/update-transaction")
    public Transaction updateTransaction(@RequestBody Transaction transaction) {
        User currentUser = userService.getLoggedInUser();
        Transaction currentTransaction = transactionService.getTransactionById(transaction.getId());

        if(!currentTransaction.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this transaction");
        }
        transaction.setUser(currentUser);
        return transactionService.updateTransaction(transaction);
    }

    @DeleteMapping("/delete-transaction/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable ("id") Long id) {
        User currentUser = userService.getLoggedInUser();
        Transaction currentTransaction = transactionService.getTransactionById(id);

        if(!currentTransaction.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this transaction");
        }
        transactionService.deleteTransaction(id);
    }

    @GetMapping("/total-income")
    public Double getTotalIncome() {
        User currentUser = userService.getLoggedInUser();
        return transactionService.getTotalIncomeByUserId(currentUser.getId());
    }

    @GetMapping("/total-expense")
    public Double getTotalExpense() {
        User currentUser = userService.getLoggedInUser();
        return transactionService.getTotalExpenseByUserId(currentUser.getId());
    }
}
