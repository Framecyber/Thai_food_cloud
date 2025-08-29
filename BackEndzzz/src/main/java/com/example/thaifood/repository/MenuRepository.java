package com.example.thaifood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.thaifood.model.MenuItem;
//import com.example.thaifood.model.Category;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByItemNameContainingIgnoreCase(String itemName);
}
