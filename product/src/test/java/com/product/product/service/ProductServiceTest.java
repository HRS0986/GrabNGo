package com.product.product.service;

import com.product.product.dto.ProductDto;
import com.product.product.entity.Product;
import com.product.product.exception.ResourceNotFoundException;
import com.product.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FilteredProducts filteredProducts;

    @Mock
    private ModelMapper modelMapper;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setProductId(1);
        product.setProductName("Test Product");
        product.setProductDescription("Test Description");
        product.setProductPrice(100.0);
        product.setProductQuantity(10);
        product.setActive(true);
        product.setAvailable(true);
        product.setImageUrl("test-url");
        product.setCategoryId(1);

        productDto = new ProductDto();
        productDto.setProductId(1);
        productDto.setProductName("Test Product");
        productDto.setProductDescription("Test Description");
        productDto.setProductPrice(100.0);
        productDto.setProductQuantity(10);
        productDto.setActive(true);
        productDto.setAvailable(true);
        productDto.setImageUrl("test-url");
        productDto.setCategoryId(1);
    }

    @Test
    public void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = new PageImpl<>(Collections.singletonList(product));
        when(filteredProducts.getFilteredProducts(1, 50.0, 200.0, "Test", pageable)).thenReturn(products);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        Page<ProductDto> result = productService.getAllProducts(1, 50.0, 200.0, "Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getProductName());
    }

    @Test
    public void testGetProductById_Found() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1);

        assertNotNull(result);
        assertEquals("Test Product", result.getProductName());
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1));
    }

    @Test
    public void testSaveProduct() {
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.saveProduct(productDto);

        assertNotNull(result);
        assertEquals("Test Product", result.getProductName());
    }

    @Test
    public void testUpdateProduct_Found() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1, productDto);

        assertNotNull(result);
        assertEquals("Test Product", result.getProductName());
    }

    @Test
    public void testUpdateProduct_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1, productDto));
    }

    @Test
    public void testDeleteOrTrashProduct_Delete() {
        product.setActive(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Map<String, Object> result = productService.deleteOrTrashProduct(1);

        verify(productRepository, times(1)).softDeleteById(1);
        assertEquals("Product deleted successfully", result.get("messege"));
    }

    @Test
    public void testDeleteOrTrashProduct_Restore() {
        product.setActive(false);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Map<String, Object> result = productService.deleteOrTrashProduct(1);

        verify(productRepository, times(1)).restoreProductById(1);
        assertEquals("Product restored successfully", result.get("messege"));
    }

    @Test
    public void testDeleteOrTrashProduct_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteOrTrashProduct(1));
    }

    @Test
    public void testDeleteProductsByCategory_Delete() {
        Map<String, Object> result = productService.deleteProductsByCategory(1, false);

        verify(productRepository, times(1)).softDeleteByCategoryId(1);
        assertEquals("Products deleted successfully", result.get("messege"));
    }

    @Test
    public void testDeleteProductsByCategory_Restore() {
        Map<String, Object> result = productService.deleteProductsByCategory(1, true);

        verify(productRepository, times(1)).restoreByCategoryId(1);
        assertEquals("Products restored successfully", result.get("messege"));
    }
}
