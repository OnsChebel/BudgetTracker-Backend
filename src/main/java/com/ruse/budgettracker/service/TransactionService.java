package com.ruse.budgettracker.service;

import com.ruse.budgettracker.dto.CategoryTotal;
import com.ruse.budgettracker.model.Account;
import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.model.Transaction;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.repository.AccountRepository;
import com.ruse.budgettracker.repository.CategoryRepository;
import com.ruse.budgettracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction, User user) {
        transaction.setId(null);
        transaction.setUser(user);
        validateAndLink(transaction, user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(long id) {
        return transactionRepository.findByUserId(id);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @Transactional
    public Transaction updateTransaction(Transaction transaction, User user) {
        if (transaction.getId() == null) {
            throw bad("Transaction id is required");
        }
        Transaction existing = getTransactionById(transaction.getId());
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this transaction");
        }
        transaction.setUser(user);
        validateAndLink(transaction, user);
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

    public List<CategoryTotal> getSummaryByCategory(Long userId) {
        Map<Long, double[]> totals = new HashMap<>();

        for (Object[] row : transactionRepository.sumByCategoryAndType(userId)) {
            Long categoryId = ((Number) row[0]).longValue();
            String type = row[1] == null ? "" : row[1].toString().toUpperCase();
            double sum = ((Number) row[2]).doubleValue();

            double[] t = totals.computeIfAbsent(categoryId, k -> new double[2]);
            if (type.equals("INCOME")) {
                t[0] += sum;
            } else if (type.equals("EXPENSE")) {
                t[1] += sum;
            }
        }

        List<CategoryTotal> result = new ArrayList<>();
        for (Category c : categoryRepository.findByUserId(userId)) {
            double[] t = totals.getOrDefault(c.getId(), new double[2]);
            result.add(new CategoryTotal(c.getId(), c.getName(), c.getIcon(), round2(t[0]), round2(t[1])));
        }
        return result;
    }

    private void validateAndLink(Transaction t, User user) {
        if (t.getAmount() == null || t.getAmount() <= 0) {
            throw bad("Amount must be greater than zero");
        }
        if (t.getTransactionDate() == null) {
            throw bad("Date is required");
        }

        String type = t.getType() == null ? "" : t.getType().trim().toUpperCase();
        if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
            throw bad("Type must be INCOME or EXPENSE");
        }
        t.setType(type);

        if (t.getAccount() == null || t.getAccount().getId() == null) {
            throw bad("Account is required");
        }
        if (t.getCategory() == null || t.getCategory().getId() == null) {
            throw bad("Category is required");
        }

        Account account = accountRepository.findById(t.getAccount().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Category category = categoryRepository.findById(t.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!account.getUser().getId().equals(user.getId()) || !category.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account or category does not belong to you");
        }

        t.setAccount(account);
        t.setCategory(category);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}