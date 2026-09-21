package com.springboot.FoodOrderingSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.FoodOrderingSystem.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByItemNameContainingIgnoreCase(String itemName);

    List<MenuItem> findByRestaurantId(Long restaurantId);

    List<MenuItem> findAllByOrderByPriceAsc();
}
