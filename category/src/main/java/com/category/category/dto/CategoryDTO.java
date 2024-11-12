package com.category.category.dto;

import lombok.Data;

@Data
public class CategoryDTO {

    private Integer categoryId;
    private String categoryName;
    private String description;
    private Boolean isActive;
}
