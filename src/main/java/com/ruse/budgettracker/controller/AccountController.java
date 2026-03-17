package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.repository.AccountRepository;
import com.ruse.budgettracker.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/Account")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/newaccount")
    public ResponseEntity<Account> createAccount(@RequestBody Account account){
        Account newAccount = accountService.createAccount(account);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @GetMapping("/allaccounts")
    public ResponseEntity<List<Account>> getAllAccounts(){
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable ("id") Long id){
        Account account = accountService.getAccountById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping("/updateaccount")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account){
        Account updatedAccount = accountService.updateAccount(account);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping("/deleteaccount/{id}")
    public ResponseEntity<?> deleteAccountById(@PathVariable ("id") Long id){
        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
