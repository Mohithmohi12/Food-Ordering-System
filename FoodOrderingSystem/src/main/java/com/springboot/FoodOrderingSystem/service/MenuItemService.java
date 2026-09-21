package com.springboot.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.entity.Restaurant;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.MenuItemRepository;
import com.springboot.FoodOrderingSystem.repository.RestaurantRepository;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public ResponseEntity<ResponseStructure<MenuItem>> addItemToRestaurant(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        validatePrice(menuItem.getPrice());

        menuItem.setRestaurant(restaurant);
        MenuItem saved = menuItemRepository.save(menuItem);

        ResponseStructure<MenuItem> structure = new ResponseStructure<MenuItem>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Menu item added successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItems() {
        List<MenuItem> items = menuItemRepository.findAll();

        ResponseStructure<List<MenuItem>> structure = new ResponseStructure<List<MenuItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu items fetched successfully")
                .setData(items);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<MenuItem>> getItemById(Long id) {
        MenuItem item = findItemOrThrow(id);

        ResponseStructure<MenuItem> structure = new ResponseStructure<MenuItem>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu item fetched successfully")
                .setData(item);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<MenuItem>> updatePriceAndAvailability(Long id, Double price,
            Boolean availability) {
        MenuItem menuItem = findItemOrThrow(id);

        if (price != null) {
            validatePrice(price);
            menuItem.setPrice(price);
        }
        if (availability != null) {
            menuItem.setAvailability(availability);
        }

        MenuItem updated = menuItemRepository.save(menuItem);

        ResponseStructure<MenuItem> structure = new ResponseStructure<MenuItem>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu item updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<MenuItem>>> sortByPrice() {
        List<MenuItem> items = menuItemRepository.findAllByOrderByPriceAsc();

        ResponseStructure<List<MenuItem>> structure = new ResponseStructure<List<MenuItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu items fetched successfully")
                .setData(items);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<MenuItem>>> getItemsByName(String name) {
        List<MenuItem> items = menuItemRepository.findByItemNameContainingIgnoreCase(name);

        ResponseStructure<List<MenuItem>> structure = new ResponseStructure<List<MenuItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu items fetched successfully")
                .setData(items);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItemsInRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);

        ResponseStructure<List<MenuItem>> structure = new ResponseStructure<List<MenuItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu items fetched successfully")
                .setData(items);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    private MenuItem findItemOrThrow(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    private void validatePrice(Double price) {
        if (price == null || price < 0) {
            throw new InvalidRequestException("Menu item price cannot be negative");
        }
    }
}
