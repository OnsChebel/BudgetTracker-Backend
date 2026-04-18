package com.ruse.budgettracker.service;

import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.model.Transaction;
import com.ruse.budgettracker.repository.AccountRepository;
import com.ruse.budgettracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        if (transaction.getAccount() != null && transaction.getAccount().getId() != null) {

            Account account = accountRepository.findById(transaction.getAccount().getId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if ("INCOME".equalsIgnoreCase(transaction.getType())) {
                account.setInitialBalance(account.getInitialBalance() + transaction.getAmount());
            } else if ("EXPENSE".equalsIgnoreCase(transaction.getType())) {
                account.setInitialBalance(account.getInitialBalance() - transaction.getAmount());
            }

            accountRepository.save(account);
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(long id) {
        return transactionRepository.findByUserId(id);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Double getTotalIncomeByUserId(Long userId) {
        Double income = transactionRepository.getTotalIncomeByUserId(userId);
        return income == null ? 0.0 : income;
    }

    public Double getTotalExpenseByUserId(Long userId) {
        Double expense = transactionRepository.getTotalExpenseByUserId(userId);
        return expense == null ? 0.0 : expense;
    }
}
