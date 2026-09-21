package com.springboot.FoodOrderingSystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.service.MenuItemService;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<MenuItem>> addItemToRestaurant(@PathVariable Long restaurantId,
            @RequestBody MenuItem menuItem) {
        return menuItemService.addItemToRestaurant(restaurantId, menuItem);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItems() {
        return menuItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<MenuItem>> getItemById(@PathVariable Long id) {
        return menuItemService.getItemById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<MenuItem>> updatePriceAndAvailability(@PathVariable Long id,
            @RequestBody MenuItem menuItemRequest) {
        return menuItemService.updatePriceAndAvailability(id, menuItemRequest.getPrice(),
                menuItemRequest.getAvailability());
    }

    @GetMapping("/sort-by-price")
    public ResponseEntity<ResponseStructure<List<MenuItem>>> sortByPrice() {
        return menuItemService.sortByPrice();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseStructure<List<MenuItem>>> getItemsByName(@PathVariable String name) {
        return menuItemService.getItemsByName(name);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItemsInRestaurant(
            @PathVariable Long restaurantId) {
        return menuItemService.getAllItemsInRestaurant(restaurantId);
    }
}
