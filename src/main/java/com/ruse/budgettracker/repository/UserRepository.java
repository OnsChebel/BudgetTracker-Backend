package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Category;
import com.ruse.budgettracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
