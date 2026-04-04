package com.ruse.budgettracker.service;


import com.ruse.budgettracker.dto.LoanScheduleRow;
import com.ruse.budgettracker.model.Loan;
import com.ruse.budgettracker.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    public Loan updateLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long id){
        loanRepository.deleteById(id);
    }

    public Double calculateMonthlyPayment(Loan loan) {
        Double r= loan.getAnnualInterest()/12/100;
        Double A;
        if (loan.getAnnualInterest() != 0){
            A=(loan.getPrincipal()*r*Math.pow(1+r,loan.getLoanTerm()))/(Math.pow(1+r,loan.getLoanTerm())-1);
            A = Math.round(A * 100.0) / 100.0;
            return A;}
        else {
            return loan.getPrincipal()/loan.getLoanTerm();
        }
    }

    public List<LoanScheduleRow> generateLoanSchedule(Loan loan) {
        List<LoanScheduleRow> schedule = new ArrayList<>();
        Double monthlyPayment = calculateMonthlyPayment(loan);
        Double remainingPrincipal = loan.getPrincipal();
        Double r = loan.getAnnualInterest() / 12 / 100;

        for (Integer month = 1; month <= loan.getLoanTerm(); month++) {

            //this month's interest (Remaining Principal * r)
            Double currentInterest = remainingPrincipal*r;
            //this month's principal payment (Monthly Payment - Interest)
            Double currentPrincipal = monthlyPayment-currentInterest;
            //the remaining principal (Previous Remaining - Principal Paid)
            remainingPrincipal = remainingPrincipal-currentPrincipal;

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
}
