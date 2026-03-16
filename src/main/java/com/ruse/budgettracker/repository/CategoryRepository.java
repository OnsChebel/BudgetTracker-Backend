package com.ruse.budgettracker.repository;

import com.ruse.budgettracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}