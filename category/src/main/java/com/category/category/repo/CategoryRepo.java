package com.category.category.repo;

import com.category.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryIdAndIsActiveTrue(Integer categoryId);

}