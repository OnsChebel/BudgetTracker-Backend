package com.ruse.budgettracker.service;

import com.ruse.budgettracker.dto.AccountBalance;
import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.repository.AccountRepository;
import com.ruse.budgettracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(Account account) {
        account.setId(null);
        validate(account);
        return accountRepository.save(account);
    }

    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    public Account updateAccount(Account account) {
        validate(account);
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        try {
            accountRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This account still has transactions. Delete them first.");
        }
    }

    public Double getTotalBalanceByUserId(Long userId) {
        double initial = orZero(accountRepository.getTotalBalanceByUserId(userId));
        double income = orZero(transactionRepository.getTotalIncomeByUserId(userId));
        double expense = orZero(transactionRepository.getTotalExpenseByUserId(userId));
        return initial + income - expense;
    }

    public List<AccountBalance> getBalances(Long userId) {
        Map<Long, Double> net = new HashMap<>();

        for (Object[] row : transactionRepository.sumByAccountAndType(userId)) {
            Long accountId = ((Number) row[0]).longValue();
            String type = row[1] == null ? "" : row[1].toString().toUpperCase();
            double sum = ((Number) row[2]).doubleValue();

            double signed = type.equals("INCOME") ? sum : type.equals("EXPENSE") ? -sum : 0.0;
            net.merge(accountId, signed, Double::sum);
        }

        List<AccountBalance> result = new ArrayList<>();
        for (Account a : accountRepository.findByUserId(userId)) {
            double balance = orZero(a.getInitialBalance()) + net.getOrDefault(a.getId(), 0.0);
            result.add(new AccountBalance(a.getId(), a.getName(), a.getCurrency(),
                    Math.round(balance * 100.0) / 100.0));
        }
        return result;
    }

    private void validate(Account account) {
        if (account.getName() == null || account.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account name is required");
        }
        account.setName(account.getName().trim());
        if (account.getInitialBalance() == null) {
            account.setInitialBalance(0.0);
        }
    }

    private double orZero(Double value) {
        return value == null ? 0.0 : value;
    }
}