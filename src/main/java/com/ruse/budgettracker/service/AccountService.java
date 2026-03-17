package com.ruse.budgettracker.service;


import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account){
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id){
        return accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account not found"));
    }

    public Account updateAccount(Account account){
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id){
        accountRepository.deleteById(id);
    }
}
