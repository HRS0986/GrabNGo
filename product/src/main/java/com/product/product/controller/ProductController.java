package com.product.product.controller;
import com.product.product.dto.ProductDto;
import com.product.product.dto.SuccessResponse;
import com.product.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDto>> getProductById(@PathVariable int id) {
        ProductDto productDto = productService.getProductById(id);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("fetched product",productDto,HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<ProductDto>> addProduct(@RequestBody ProductDto productDto) {
        ProductDto savedproductdto = productService.saveProduct(productDto);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("saved product",savedproductdto,HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDto>> updateProduct(@PathVariable int id, @RequestBody ProductDto productDto) {
        ProductDto updatedproductdto = productService.updateProduct(id, productDto);
        SuccessResponse<ProductDto> success = new SuccessResponse<>("updated product",updatedproductdto,HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<ProductDto>> deleteOrTrashProduct(@RequestParam int id) {
        Map<String,Object> datamap = productService.deleteOrTrashProduct(id);
        String message = (String) datamap.get("messege");
        SuccessResponse<ProductDto> success = new SuccessResponse<>(message,null,HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping("deleteByCategory")
    public ResponseEntity<SuccessResponse<ProductDto>> deleteProductsByCategoryId(@RequestParam int categoryId,@RequestParam boolean isDeleted) {
        Map<String,Object> datamap = productService.deleteProductsByCategory(categoryId,isDeleted);
        String message = (String) datamap.get("messege");
        SuccessResponse<ProductDto> success = new SuccessResponse<>(message,null,HttpStatus.OK);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }


}
