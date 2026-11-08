package com.ruse.budgettracker.dto;

public record CategoryTotal(Long categoryId, String name, String icon, Double income, Double expense) {
}