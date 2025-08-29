package com.example.thaifood.dto;

import com.example.thaifood.model.Category;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String createdAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Default constructor
    public CategoryDTO() {
    }

    // Constructor from entity
    public CategoryDTO(Category category) {
        if (category != null) {
            this.id = category.getId();
            this.name = category.getName();
            this.description = category.getDescription();
            if (category.getCreatedAt() != null) {
                this.createdAt = category.getCreatedAt().format(formatter);
            }
        }
    }

    // Convert to entity
    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setDescription(this.description);
        if (this.createdAt != null) {
            try {
                category.setCreatedAt(LocalDateTime.parse(this.createdAt, formatter));
            } catch (Exception e) {
                // Handle parsing error if needed
            }
        }
        return category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
