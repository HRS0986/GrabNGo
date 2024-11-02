package com.product.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private int productId;
    private String productName;
    private String productDescription;
    private double productPrice;
    private int productQuantity;
    private String ProductImageUrl;
    private int productCategoryId;
    private boolean isAvailable = true;
    private boolean isActive=true;

}
