package com.product.product.service;

import com.product.product.dto.ProductDto;
import com.product.product.entity.Product;
import com.product.product.exception.ResourceNotFoundException;
import com.product.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FilteredProducts filteredProducts;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        product.setProductName("Test Product");
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

        when(filteredProducts.getFilteredProducts(1, null, null, null, pageable)).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(new ProductDto());

        Page<ProductDto> result = productService.getAllProducts(1, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(filteredProducts, times(1)).getFilteredProducts(1, null, null, null, pageable);
    }

    @Test
    void testGetProductById_ProductExists() {
        Product product = new Product();
        product.setActive(true);
        product.setProductId(1);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductDto productDto = new ProductDto();
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1);

        System.out.println("Result: " + result);

        assertNotNull(result, "ProductDto should not be null");
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testGetProductById_ProductNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1));
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testSaveProduct() {
        ProductDto productDto = new ProductDto();
        Product product = new Product();
        Product savedProduct = new Product();

        when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(modelMapper.map(savedProduct, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.saveProduct(productDto);

        assertNotNull(result, "Saved productDto should not be null");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_ProductExists() {
        ProductDto productDto = new ProductDto();
        Product product = new Product();
        product.setProductId(1);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1, productDto);

        assertNotNull(result, "Updated productDto should not be null");
        assertEquals(productDto, result, "Returned ProductDto should match the input ProductDto");
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        ProductDto productDto = new ProductDto();

        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1, productDto));
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteOrTrashProduct_ProductExists() {
        Product product = new Product();
        product.setActive(true);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Map<String, Object> result = productService.deleteOrTrashProduct(1);

        assertNotNull(result, "Result map should not be null");
        assertEquals("Product deleted successfully", result.get("message"));
        verify(productRepository, times(1)).softDeleteById(1);
    }

    @Test
    void testDeleteOrTrashProduct_ProductNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteOrTrashProduct(1));
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteOrTrashProduct_ProductRestored() {
        Product product = new Product();
        product.setActive(false);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Map<String, Object> result = productService.deleteOrTrashProduct(1);

        assertNotNull(result, "Result map should not be null");
        assertEquals("Product restored successfully", result.get("message"));
        verify(productRepository, times(1)).restoreProductById(1);
    }
}
