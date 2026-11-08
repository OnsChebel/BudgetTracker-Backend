package com.ruse.budgettracker.service;

import com.ruse.budgettracker.dto.LoanScheduleRow;
import com.ruse.budgettracker.model.Loan;
import com.ruse.budgettracker.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan createLoan(Loan loan) {
        loan.setId(null);
        validate(loan);
        return loanRepository.save(loan);
    }

    public List<Loan> findByUserId(Long id) {
        return loanRepository.findByUserId(id);
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    public Loan updateLoan(Loan loan) {
        validate(loan);
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    public Double calculateMonthlyPayment(Loan loan) {
        Double r = loan.getAnnualInterest() / 12 / 100;
        Double A;
        if (loan.getAnnualInterest() != 0) {
            A = (loan.getPrincipal() * r * Math.pow(1 + r, loan.getLoanTerm())) / (Math.pow(1 + r, loan.getLoanTerm()) - 1);
        } else {
            A = loan.getPrincipal() / loan.getLoanTerm();
        }
        return Math.round(A * 100.0) / 100.0;
    }

    public List<LoanScheduleRow> generateLoanSchedule(Loan loan) {
        List<LoanScheduleRow> schedule = new ArrayList<>();
        Double monthlyPayment = calculateMonthlyPayment(loan);
        Double remainingPrincipal = loan.getPrincipal();
        Double r = loan.getAnnualInterest() / 12 / 100;

        for (Integer month = 1; month <= loan.getLoanTerm(); month++) {

            Double currentInterest = remainingPrincipal * r;
            Double currentPrincipal = monthlyPayment - currentInterest;
            remainingPrincipal = remainingPrincipal - currentPrincipal;

            if (remainingPrincipal < 0) {
                remainingPrincipal = 0.0;
            }

            currentInterest = Math.round(currentInterest * 100.0) / 100.0;
            currentPrincipal = Math.round(currentPrincipal * 100.0) / 100.0;
            Double roundedRemaining = Math.round(remainingPrincipal * 100.0) / 100.0;

            LoanScheduleRow loanScheduleRow = new LoanScheduleRow(month, currentPrincipal, currentInterest, monthlyPayment, roundedRemaining);
            schedule.add(loanScheduleRow);
        }

        return schedule;
    }

    private void validate(Loan loan) {
        if (loan.getPrincipal() == null || loan.getPrincipal() <= 0) {
            throw bad("Principal must be greater than zero");
        }
        if (loan.getAnnualInterest() == null || loan.getAnnualInterest() < 0 || loan.getAnnualInterest() > 100) {
            throw bad("Annual interest rate must be between 0 and 100");
        }
        if (loan.getLoanTerm() == null || loan.getLoanTerm() < 1 || loan.getLoanTerm() > 600) {
            throw bad("Loan term must be between 1 and 600 months");
        }
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}