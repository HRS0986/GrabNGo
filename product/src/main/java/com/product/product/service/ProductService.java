package com.product.product.service;
import com.product.product.dto.ProductDto;
import com.product.product.entity.Product;
import com.product.product.exception.ResourceNotFoundException;
import com.product.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final FilteredProducts filteredProducts;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper, FilteredProducts filteredProducts) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.filteredProducts = filteredProducts;
    }

    public Page<ProductDto> getAllProducts(int categoryId, Double minPrice, Double maxPrice, String search, Pageable pageable) {
        // Fetch paginated products from the filteredProducts layer
        Page<Product> products = filteredProducts.getFilteredProducts(categoryId, minPrice, maxPrice, search, pageable);
        // Map Product to ProductDto and return the page of ProductDto
        return products.map(product -> modelMapper.map(product, ProductDto.class));
    }

    public ProductDto getProductById(int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && product.get().isActive()) {
            return modelMapper.map(product, ProductDto.class);
        }
        else{
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

    }

    public ProductDto saveProduct(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    public ProductDto updateProduct(int id,ProductDto productDto) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {

            product.get().setProductName(productDto.getProductName());
            product.get().setProductDescription(productDto.getProductDescription());
            product.get().setProductPrice(productDto.getProductPrice());
            product.get().setProductQuantity(productDto.getProductQuantity());
            product.get().setActive(productDto.isActive());
            product.get().setAvailable(productDto.isAvailable());
            product.get().setImageUrl(productDto.getImageUrl());
            product.get().setCategoryId(productDto.getCategoryId());

            Product updatedProduct = productRepository.save(product.get());

            return modelMapper.map(updatedProduct, ProductDto.class);

        }
        else{
            throw new ResourceNotFoundException("Product with id " + productDto.getProductId() + " not found");
        }
    }

    public Map<String, Object>  deleteOrTrashProduct(int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Map<String,Object> dataMap = new HashMap<>();
            if (product.get().isActive()) {
                productRepository.softDeleteById(id);
//                ProductDto deletedProduct = modelMapper.map(product.get(), ProductDto.class);
//                dataMap.put("productDto", deletedProduct);
                dataMap.put("messege", "Product deleted successfully");
            }
            else {
                productRepository.restoreProductById(id);
//                ProductDto restoredProduct = modelMapper.map(product.get(), ProductDto.class);
//                dataMap.put("productDto", restoredProduct);
                dataMap.put("messege", "Product restored successfully");
            }
            return dataMap;
        }
        else{
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
    }

    @Transactional
    public Map<String,Object> deleteProductsByCategory(int categoryId,boolean isDeleted) {
        Map<String,Object> dataMap = new HashMap<>();
        if (isDeleted) {
            productRepository.restoreByCategoryId(categoryId);
            dataMap.put("messege", "Products restored successfully");
        }
        else {
            productRepository.softDeleteByCategoryId(categoryId);
            dataMap.put("messege", "Products deleted successfully");
        }
        return dataMap;
    }

}
