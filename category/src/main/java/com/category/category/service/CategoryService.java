package com.category.category.service;

import com.category.category.dto.CategoryDTO;
import com.category.category.model.Category;
import com.category.category.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());
        category.setIsActive(categoryDTO.getIsActive());

        Category savedCategory = categoryRepo.save(category);
        return mapToDTO(savedCategory);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Integer categoryId) {
        Optional<Category> category = categoryRepo.findByCategoryIdAndIsActiveTrue(categoryId);
        return category.map(this::mapToDTO).orElse(null);
    }

    public CategoryDTO updateCategory(Integer categoryId, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setCategoryName(categoryDTO.getCategoryName());
            category.setDescription(categoryDTO.getDescription());
            category.setIsActive(categoryDTO.getIsActive());

            Category updatedCategory = categoryRepo.save(category);
            return mapToDTO(updatedCategory);
        }
        return null;
    }

    public boolean softDeleteCategory(Integer categoryId) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setIsActive(false);
            categoryRepo.save(category);
            return true;
        }
        return false;
    }

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setIsActive(category.getIsActive());
        return categoryDTO;
    }
}