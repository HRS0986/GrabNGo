package com.product.product.controller;

import com.product.product.dto.ProductDto;
import com.product.product.exception.ImageUploadEcxeption;
import com.product.product.response.SuccessResponse;
import com.product.product.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ProductDto>>> getProducts(
            @RequestParam(value = "categoryId", required = false, defaultValue = "-1") int categoryId,
            @RequestParam(value = "sortBy", defaultValue = "productName") String sortBy,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "sortDir", defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Get paginated product data from the service layer
        Page<ProductDto> productPage = productService.getAllProducts(categoryId, minPrice, maxPrice, search, pageable);

        // Wrap the paginated results in SuccessResponse
        SuccessResponse<List<ProductDto>> success = new SuccessResponse<>(
                "Fetched all products",
                productPage.getContent(),
                HttpStatus.OK
        );
        // Return the paginated response
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDto>> getProductById(@PathVariable int id) {
        ProductDto productDto = productService.getProductById(id);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("fetched product", productDto, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<ProductDto>> addProduct(@ModelAttribute ProductDto productDto,@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = productService.saveImage(file,uploadDir);
        productDto.setImageUrl(filePath);
        ProductDto savedproductdto = productService.saveProduct(productDto);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("saved product", savedproductdto, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDto>> updateProduct(@PathVariable int id, @RequestBody ProductDto productDto) {
        ProductDto updatedproductdto = productService.updateProduct(id, productDto);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("updated product", updatedproductdto, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<ProductDto>> deleteOrTrashProduct(@RequestParam int id) {
        Map<String, Object> datamap = productService.deleteOrTrashProduct(id);
        String message = (String) datamap.get("messege");
        SuccessResponse<ProductDto> success = new SuccessResponse<>(message, null, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping("/deleteByCategory")
    public ResponseEntity<SuccessResponse<ProductDto>> deleteProductsByCategoryId(@RequestParam int categoryId, @RequestParam boolean isDeleted) {
        Map<String, Object> datamap = productService.deleteProductsByCategory(categoryId, isDeleted);
        String message = (String) datamap.get("messege");
        SuccessResponse<ProductDto> success = new SuccessResponse<>(message, null, HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }


    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
