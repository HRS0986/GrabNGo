package com.category.category.controller;

import com.category.category.dto.CategoryDTO;
import com.category.category.responses.SuccessResponse;
import com.category.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<SuccessResponse<CategoryDTO>> addCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.addCategory(categoryDTO);
        SuccessResponse<CategoryDTO> success = new SuccessResponse<>("Category added", createdCategory, HttpStatus.CREATED);
        return new ResponseEntity<>(success, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        SuccessResponse<List<CategoryDTO>> success = new SuccessResponse<>("fetch categories", categories, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<SuccessResponse<CategoryDTO>> getCategoryById(@PathVariable int categoryId) {
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        SuccessResponse<CategoryDTO> success = new SuccessResponse<>("Category found", category, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<SuccessResponse<CategoryDTO>> updateCategory(
            @PathVariable int categoryId,
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        SuccessResponse<CategoryDTO> success = new SuccessResponse<>("Category updated", updatedCategory, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Void> TrashOrRestore(@RequestParam int categoryId) {
        boolean deleted = categoryService.softDeleteOrRestoreCategory(categoryId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


