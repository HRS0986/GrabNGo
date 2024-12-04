package com.product.product.service;

import com.product.product.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilteredProductsTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Product> criteriaQuery;

    @Mock
    private CriteriaQuery<Long> countQuery;

    @Mock
    private Root<Product> root;

    @Mock
    private TypedQuery<Product> typedQuery;

    @Mock
    private TypedQuery<Long> countTypedQuery;

    @InjectMocks
    private FilteredProducts filteredProducts;

    @BeforeEach
    void setUp() {
        // Detailed mocking setup
        lenient().when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        lenient().when(criteriaBuilder.createQuery(Product.class)).thenReturn(criteriaQuery);
        lenient().when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        lenient().when(criteriaQuery.from(Product.class)).thenReturn(root);
        lenient().when(countQuery.from(Product.class)).thenReturn(root);
    }

    @Test
    void testGetFilteredProductsWithNoFilters() {
        // Prepare test data and mocking
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = createMockProducts(5);

        // Detailed mocking for product query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockProducts);

        // Detailed mocking for count query
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(5L);

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(0, null, null, null, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(5, result.getTotalElements());

        // Verify interactions
        verify(entityManager).createQuery(criteriaQuery);
        verify(entityManager).createQuery(countQuery);
    }

    @Test
    void testGetFilteredProductsByCategoryId() {
        // Prepare test data and mocking
        int categoryId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = createMockProducts(3);

        // Detailed mocking for product query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockProducts);

        // Detailed mocking for count query
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(3L);

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(categoryId, null, null, null, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());

        // Verify predicates include category filter
        verify(criteriaBuilder).equal(any(), eq(categoryId));
    }

    @Test
    void testGetFilteredProductsByPriceRange() {
        // Prepare test data and mocking
        double minPrice = 10.0;
        double maxPrice = 100.0;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = createMockProducts(4);

        // Detailed mocking for product query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockProducts);

        // Detailed mocking for count query
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(4L);

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(0, minPrice, maxPrice, null, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(4, result.getContent().size());
        assertEquals(4, result.getTotalElements());

        // Verify predicates include price range filters
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(minPrice));
        verify(criteriaBuilder).lessThanOrEqualTo(any(), eq(maxPrice));
    }

    @Test
    void testGetFilteredProductsBySearch() {
        // Prepare test data and mocking
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = createMockProducts(2);

        // Detailed mocking for product query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockProducts);

        // Detailed mocking for count query
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(2L);

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(0, null, null, searchTerm, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());

        // Verify predicate includes search filter
        verify(criteriaBuilder).like(any(), contains(searchTerm.toLowerCase()));
    }

    @Test
    void testGetFilteredProductsWithSorting() {
        // Prepare test data and mocking
        Pageable pageable = PageRequest.of(0, 10, Sort.by("productPrice").descending());
        List<Product> mockProducts = createMockProducts(3);

        // Detailed mocking for product query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockProducts);

        // Detailed mocking for count query
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(3L);

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(0, null, null, null, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());

        // Verify sorting was applied
        verify(criteriaBuilder).desc(any());
    }

    @Test
    void testGetFilteredProductsWithQueryException() {
        // Prepare test data and mocking
        Pageable pageable = PageRequest.of(0, 10);

        // Simulate query execution failure
        when(entityManager.createQuery(criteriaQuery)).thenThrow(new RuntimeException("Query execution failed"));

        // Execute the method
        Page<Product> result = filteredProducts.getFilteredProducts(0, null, null, null, pageable);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Helper method to create mock products for testing
    private List<Product> createMockProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setProductId(i + 1);
            product.setProductName("Test Product " + (i + 1));
            product.setProductPrice(10.0 * (i + 1));
            products.add(product);
        }
        return products;
    }
}

