package com.ruse.budgettracker.controller;


import com.ruse.budgettracker.dto.LoanScheduleRow;
import com.ruse.budgettracker.model.Loan;
import com.ruse.budgettracker.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/new-loan")
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan){
        Loan newLoan = loanService.createLoan(loan);
        return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
    }

    @GetMapping("/all-loans")
    public List<Loan> getAllLoans(){
        return loanService.getAllLoans();
    }

    @GetMapping("/show-loan/{id}")
    public Loan getLoanById(@PathVariable ("id") Long id){
        return loanService.getLoanById(id);
    }

    @PutMapping("/update-loan")
    public Loan updateLoan(@RequestBody Loan loan){
        return loanService.updateLoan(loan);
    }

    @DeleteMapping("/delete-loan/{id}")
    public void deleteLoan(@PathVariable ("id") Long id){
        loanService.deleteLoan(id);
    }

    @GetMapping("/schedule/{id}")
    public List<LoanScheduleRow> getLoanSchedule(@PathVariable("id") Long id) {
        Loan loan = loanService.getLoanById(id);
        return loanService.generateLoanSchedule(loan);
    }

}
