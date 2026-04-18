package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);

    @Query("SELECT SUM(a.initialBalance) FROM Account a WHERE a.user.id = :userId")
    Double getTotalBalanceByUserId(@Param("userId") Long userId);
}