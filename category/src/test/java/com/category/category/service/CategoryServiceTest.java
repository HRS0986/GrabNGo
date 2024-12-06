package com.category.category.service;

import com.category.category.dto.CategoryDTO;
import com.category.category.exception.ResourceNotFoundException;
import com.category.category.model.Category;
import com.category.category.repo.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryDTO categoryDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up test data
        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Test Category");
        categoryDTO.setDescription("Test Description");
        categoryDTO.setIsActive(true);

        category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
        category.setDescription("Test Description");
        category.setIsActive(true);
    }

    @Test
    void testAddCategory() {
        // Mock the CategoryRepo save method
        when(categoryRepo.save(any(Category.class))).thenReturn(category);

        // Call the addCategory method
        CategoryDTO result = categoryService.addCategory(categoryDTO);

        // Verify that save was called once and check the result
        verify(categoryRepo, times(1)).save(any(Category.class));
        assertNotNull(result);
        assertEquals("Test Category", result.getCategoryName());
    }

    @Test
    void testGetCategoryByIdCategoryFound() {
        // Mock the CategoryRepo findByCategoryIdAndIsActiveTrue method
        when(categoryRepo.findByCategoryIdAndIsActiveTrue(1)).thenReturn(Optional.of(category));

        // Call the getCategoryById method
        CategoryDTO result = categoryService.getCategoryById(1);

        // Verify that the category was found and returned correctly
        verify(categoryRepo, times(1)).findByCategoryIdAndIsActiveTrue(1);
        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        assertEquals("Test Category", result.getCategoryName());
    }

    @Test
    void testGetCategoryByIdCategoryNotFound() {
        // Mock the CategoryRepo findByCategoryIdAndIsActiveTrue method to return empty
        when(categoryRepo.findByCategoryIdAndIsActiveTrue(1)).thenReturn(Optional.empty());

        // Call the getCategoryById method and assert that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1));
    }

    @Test
    void testUpdateCategory() {
        // Mock the CategoryRepo findById and save methods
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepo.save(any(Category.class))).thenReturn(category);

        // Create an updated CategoryDTO
        categoryDTO.setCategoryName("Updated Category");

        // Call the updateCategory method
        CategoryDTO result = categoryService.updateCategory(1, categoryDTO);

        // Verify that save was called once and check the result
        verify(categoryRepo, times(1)).save(any(Category.class));
        assertEquals("Updated Category", result.getCategoryName());
    }

    @Test
    void testSoftDeleteCategoryFound() {
        // Mock the CategoryRepo findById method
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));

        // Mock WebClient behavior
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyInt(), anyBoolean())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Call the softDeleteOrRestoreCategory method to delete
        boolean result = categoryService.softDeleteOrRestoreCategory(1);

        // Verify interactions with WebClient and CategoryRepo
        verify(categoryRepo, times(1)).save(any(Category.class));
        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).put();
        verify(requestBodyUriSpec, times(1)).uri(anyString(), anyInt(), anyBoolean());
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(Void.class);

        // Assert the result of the soft delete
        assertTrue(result);
        assertFalse(category.getIsActive());  // Category should be deactivated
    }

    @Test
    void testDeleteRelatedProducts() {
        // Mock WebClient behaviors
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyInt(), anyBoolean())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Call the deleteRelatedProducts method
        Mono<Void> result = categoryService.deleteRelatedProducts(1, true);

        // Assert that the result is not null and verify WebClient interactions
        assertNotNull(result);
        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).put();
        verify(requestBodyUriSpec, times(1)).uri(anyString(), anyInt(), anyBoolean());
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(Void.class);
    }

    @Test
    void testSoftDeleteCategoryNotFound() {
        // Arrange: Mocking category retrieval to return empty
        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        // Act: Call the soft delete method
        boolean isDeleted = categoryService.softDeleteOrRestoreCategory(1);

        // Assert: Verify that the category was not deleted
        assertFalse(isDeleted);

        // Verify that the findById method was called once
        verify(categoryRepo, times(1)).findById(1);
    }


}
