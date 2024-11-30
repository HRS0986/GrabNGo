package com.category.category.service;
import com.category.category.dto.CategoryDTO;
import com.category.category.exception.ResourceNotFoundException;
import com.category.category.model.Category;
import com.category.category.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final WebClient.Builder webClientBuilder;

    public CategoryService(CategoryRepo categoryRepo, WebClient.Builder webClientBuilder) {
        this.categoryRepo = categoryRepo;
        this.webClientBuilder = webClientBuilder;
    }

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
                .filter(Category::getIsActive) // Filters categories with isActive = true
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public CategoryDTO getCategoryById(int categoryId) {
        Optional<Category> category = categoryRepo.findByCategoryIdAndIsActiveTrue(categoryId);
        if (category.isPresent()) {
            return mapToDTO(category.get());
        }
        throw new ResourceNotFoundException("Category not found");
    }

    public CategoryDTO updateCategory(int categoryId, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setCategoryName(categoryDTO.getCategoryName());
            category.setDescription(categoryDTO.getDescription());
            category.setIsActive(categoryDTO.getIsActive());

            Category updatedCategory = categoryRepo.save(category);
            return mapToDTO(updatedCategory);
        }
        throw new ResourceNotFoundException("Category not found");
    }

    public boolean softDeleteOrRestoreCategory(int categoryId) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);

        if (categoryOptional.isPresent()) {

            Category category = categoryOptional.get();
            if (category.getIsActive()) {
                // Delete related products
                deleteRelatedProducts(category.getCategoryId(), false).subscribe();
                category.setIsActive(false);
                categoryRepo.save(category);
                return true;
            } else {
                // restore related products
                deleteRelatedProducts(category.getCategoryId(), true).subscribe();
                category.setIsActive(true);
                categoryRepo.save(category);
                return true;
            }
        }
        return false;
    }

    public Mono<Void> deleteRelatedProducts(int categoryId, boolean isDeleted) {
        return webClientBuilder.build()
                .put()
                .uri("http://apigateway/api/v1/product/deleteByCategory?categoryId={categoryId}&isDeleted={isDeleted}", categoryId, isDeleted)
                .retrieve()
                .bodyToMono(Void.class);
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