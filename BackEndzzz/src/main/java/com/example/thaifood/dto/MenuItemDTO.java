package com.example.thaifood.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.thaifood.model.Category;
import com.example.thaifood.model.MenuItem;

public class MenuItemDTO {
    private Long idItem;
    private String itemName;
    private String description;
    private Double price;
    private String imageUrl;
    private Long categoryId;
    private Boolean isAvailable;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Default constructor
    public MenuItemDTO() {
    }

    // Constructor from entity
    public MenuItemDTO(MenuItem menuItem) {
        if (menuItem != null) {
            this.idItem = menuItem.getId();
            this.itemName = menuItem.getItemName();
            this.description = menuItem.getDescription();
            this.price = menuItem.getPrice();
            this.imageUrl = menuItem.getImageUrl();
            this.isAvailable = menuItem.getIsAvailable();
            
            // if (menuItem.getCategory() != null) {
            // this.categoryId = menuItem.getCategory().getId();
            // }
            if (menuItem.getCreatedAt() != null) {
                this.createdAt = menuItem.getCreatedAt().format(formatter);
            }
            if (menuItem.getUpdatedAt() != null) {
                this.updatedAt = menuItem.getUpdatedAt().format(formatter);
            }
        }
    }

    // Convert to entity
    public MenuItem toEntity() {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(this.idItem);
        menuItem.setItemName(this.itemName);
        menuItem.setDescription(this.description);
        menuItem.setPrice(this.price);
        menuItem.setImageUrl(this.imageUrl);
        menuItem.setIsAvailable(this.isAvailable);

        // if (this.categoryId != null) {
        //     Category category = new Category();
        //     category.setId(this.categoryId);
        //     menuItem.setCategory(category);
        // }


        if (this.createdAt != null) {
            try {
                menuItem.setCreatedAt(LocalDateTime.parse(this.createdAt, formatter));
            } catch (Exception e) {
                // handle parse error
            }
        }
        if (this.updatedAt != null) {
            try {
                menuItem.setUpdatedAt(LocalDateTime.parse(this.updatedAt, formatter));
            } catch (Exception e) {
                // handle parse error
            }
        }

        return menuItem;
    }

    // Getters and Setters
    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
