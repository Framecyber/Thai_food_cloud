package com.example.thaifood.service;

import com.example.thaifood.model.Category;
import com.example.thaifood.dto.CategoryDTO;
import com.example.thaifood.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    // Constructor Injection
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ✅ ดึงข้อมูลทั้งหมด
    public List<CategoryDTO> getAllCategories() {
        logger.info("🔍 Fetching all categories from database...");
        
        try {
            List<Category> entities = categoryRepository.findAll();
            logger.info("📊 Found {} category entities from database", entities.size());
            
            // Debug: log each entity
            for (int i = 0; i < entities.size(); i++) {
                Category entity = entities.get(i);
                logger.info("Entity {}: id={}, name={}, description={}, createdAt={}", 
                    i+1, entity.getId(), entity.getName(), entity.getDescription(), entity.getCreatedAt());
            }
            
            List<CategoryDTO> dtos = entities.stream()
                    .map(entity -> {
                        CategoryDTO dto = new CategoryDTO(entity);
                        logger.debug("Converted entity id={} to DTO id={}", entity.getId(), dto.getId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            logger.info("✅ Successfully converted {} entities to DTOs", dtos.size());
            return dtos;
            
        } catch (Exception e) {
            logger.error("❌ Error in getAllCategories: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ✅ ดึงข้อมูลตาม id
    public Optional<CategoryDTO> getCategoryById(Long id) {
        logger.info("🔍 Fetching category by id: {}", id);
        return categoryRepository.findById(id)
                .map(entity -> {
                    logger.info("✅ Found category: {}", entity.getName());
                    return new CategoryDTO(entity);
                });
    }

    // ✅ สร้างข้อมูลใหม่
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        logger.info("➕ Creating new category: {}", categoryDTO.getName());
        Category category = categoryDTO.toEntity();
        Category savedCategory = categoryRepository.save(category);
        logger.info("✅ Successfully created category with id: {}", savedCategory.getId());
        return new CategoryDTO(savedCategory);
    }

    // ✅ อัปเดตข้อมูล
    public Optional<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO) {
        logger.info("📝 Updating category id: {} with name: {}", id, categoryDTO.getName());
        return categoryRepository.findById(id)
            .map(category -> {
                category.setName(categoryDTO.getName());
                category.setDescription(categoryDTO.getDescription());
                // createdAt ไม่ควรแก้ → ให้มันเป็นค่าตอนแรกที่ insert
                Category updatedCategory = categoryRepository.save(category);
                logger.info("✅ Successfully updated category id: {}", updatedCategory.getId());
                return new CategoryDTO(updatedCategory);
            });
    }

    // ✅ ลบข้อมูล
    public boolean deleteCategory(Long id) {
        logger.info("🗑️ Deleting category id: {}", id);
        return categoryRepository.findById(id)
            .map(category -> {
                categoryRepository.delete(category);
                logger.info("✅ Successfully deleted category id: {}", id);
                return true;
            })
            .orElse(false);
    }
}