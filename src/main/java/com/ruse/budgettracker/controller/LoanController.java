package com.ruse.budgettracker.controller;


import com.ruse.budgettracker.dto.LoanScheduleRow;
import com.ruse.budgettracker.model.Loan;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.LoanService;
import com.ruse.budgettracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;
    private final UserService userService;

    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @PostMapping("/new-loan")
    public Loan createLoan(@RequestBody Loan loan){
        User currentUser = userService.getLoggedInUser();
        loan.setUser(currentUser);
        return loanService.createLoan(loan);
    }

    @GetMapping("/my-loans")
    public List<Loan> getMyLoans(){
        User currentUser = userService.getLoggedInUser();
        return loanService.findByUserId(currentUser.getId());
    }

    @GetMapping("/show-loan/{id}")
    public Loan getLoanById(@PathVariable ("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Loan loan = loanService.getLoanById(id);

        if(!loan.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this loan");
        }
        return loanService.getLoanById(id);
    }

    @PutMapping("/update-loan")
    public Loan updateLoan(@RequestBody Loan loan){
        User currentUser = userService.getLoggedInUser();
        Loan currentLoan = loanService.getLoanById(loan.getId());

        if(!currentLoan.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this loan");
        }
        loan.setUser(currentUser);
        return loanService.updateLoan(loan);
    }

    @DeleteMapping("/delete-loan/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLoan(@PathVariable ("id") Long id){
        User currentUser = userService.getLoggedInUser();
        Loan loan = loanService.getLoanById(id);

        if(!loan.getUser().getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this loan");
        }
        loanService.deleteLoan(id);
    }

    @GetMapping("/schedule/{id}")
    public List<LoanScheduleRow> getLoanSchedule(@PathVariable("id") Long id) {
        Loan loan = loanService.getLoanById(id);
        return loanService.generateLoanSchedule(loan);
    }

}
