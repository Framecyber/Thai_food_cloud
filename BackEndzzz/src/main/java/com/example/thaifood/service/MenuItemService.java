package com.example.thaifood.service;

import com.example.thaifood.model.MenuItem;
import com.example.thaifood.dto.MenuItemDTO;
import com.example.thaifood.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);
    private final MenuRepository menuItemRepository;

    // ✅ Constructor Injection
    public MenuItemService(MenuRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    // ✅ ดึงข้อมูลทั้งหมด
    public List<MenuItemDTO> getAllMenuItems() {
        logger.info("🔍 Fetching all menu items from database...");

        try {
            List<MenuItem> entities = menuItemRepository.findAll();
            logger.info("📊 Found {} menu items from database", entities.size());

            // Debug log
            for (int i = 0; i < entities.size(); i++) {
                MenuItem entity = entities.get(i);
                logger.info("Entity {}: id={}, name={}, price={}, available={}",
                        i + 1, entity.getId(), entity.getItemName(),
                        entity.getPrice(), entity.getIsAvailable());
            }

            List<MenuItemDTO> dtos = entities.stream()
                    .map(MenuItemDTO::new)
                    .collect(Collectors.toList());

            logger.info("✅ Successfully converted {} entities to DTOs", dtos.size());
            return dtos;

        } catch (Exception e) {
            logger.error("❌ Error in getAllMenuItems: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ✅ ดึงข้อมูลตาม id
    public Optional<MenuItemDTO> getMenuItemById(Long id) {
        logger.info("🔍 Fetching menu item by id: {}", id);
        return menuItemRepository.findById(id)
                .map(entity -> {
                    logger.info("✅ Found menu item: {}", entity.getItemName());
                    return new MenuItemDTO(entity);
                });
    }

    // ✅ สร้างข้อมูลใหม่
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        logger.info("➕ Creating new menu item: {}", menuItemDTO.getItemName());
        MenuItem menuItem = menuItemDTO.toEntity();
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        logger.info("✅ Successfully created menu item with id: {}", savedMenuItem.getId());
        return new MenuItemDTO(savedMenuItem);
    }

    // ✅ อัปเดตข้อมูล
    public Optional<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        logger.info("📝 Updating menu item id: {} with name: {}", id, menuItemDTO.getItemName());
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    menuItem.setItemName(menuItemDTO.getItemName());
                    menuItem.setDescription(menuItemDTO.getDescription());
                    menuItem.setPrice(menuItemDTO.getPrice());
                    menuItem.setImageUrl(menuItemDTO.getImageUrl());
                    menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
                    // createdAt ไม่แก้ → ให้มันคงไว้
                    MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
                    logger.info("✅ Successfully updated menu item id: {}", updatedMenuItem.getId());
                    return new MenuItemDTO(updatedMenuItem);
                });
    }

    // ✅ ลบข้อมูล
    public boolean deleteMenuItem(Long id) {
        logger.info("🗑️ Deleting menu item id: {}", id);
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    menuItemRepository.delete(menuItem);
                    logger.info("✅ Successfully deleted menu item id: {}", id);
                    return true;
                })
                .orElse(false);
    }
}
