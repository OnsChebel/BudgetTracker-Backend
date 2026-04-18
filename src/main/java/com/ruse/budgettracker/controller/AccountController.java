package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.UserService;
import com.ruse.budgettracker.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostMapping("/new-account")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account){
        User currentUser = userService.getLoggedInUser();
        account.setUser(currentUser);
        return accountService.createAccount(account);
    }

    @GetMapping("/my-accounts")
    public List<Account> getMyAccounts(){
        User currentUser = userService.getLoggedInUser();
        return accountService.findByUserId(currentUser.getId());
    }

    @GetMapping("/account/{id}")
    public Account getAccountById(@PathVariable("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Account account = accountService.getAccountById(id);

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this account");
        }

        return account;
    }

    @PutMapping("/update-account")
    public Account updateAccount(@RequestBody Account account){
        User currentUser = userService.getLoggedInUser();
        Account existingAccount = accountService.getAccountById(account.getId());

        if (!existingAccount.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this account");
        }

        account.setUser(currentUser);

        return accountService.updateAccount(account);
    }

    @DeleteMapping("/delete-account/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountById(@PathVariable("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Account account = accountService.getAccountById(id);

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this account");
        }

        accountService.deleteAccount(id);
    }

    @GetMapping("/total-balance")
    public Double getTotalBalance(){
        User currentUser = userService.getLoggedInUser();

        return accountService.getTotalBalanceByUserId(currentUser.getId());
    }
}