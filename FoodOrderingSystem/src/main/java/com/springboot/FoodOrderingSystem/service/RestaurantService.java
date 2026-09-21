package com.springboot.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.entity.Restaurant;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public ResponseEntity<ResponseStructure<Restaurant>> addRestaurant(Restaurant restaurant) {
        Restaurant saved = restaurantRepository.save(restaurant);

        ResponseStructure<Restaurant> structure = new ResponseStructure<Restaurant>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Restaurant added successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<List<Restaurant>>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        ResponseStructure<List<Restaurant>> structure = new ResponseStructure<List<Restaurant>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurants fetched successfully")
                .setData(restaurants);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantById(Long id) {
        Restaurant restaurant = findRestaurantOrThrow(id);

        ResponseStructure<Restaurant> structure = new ResponseStructure<Restaurant>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurant fetched successfully")
                .setData(restaurant);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Restaurant>> updateRestaurant(Long id, Restaurant restaurant) {
        Restaurant existing = findRestaurantOrThrow(id);

        if (restaurant.getName() != null) {
            existing.setName(restaurant.getName());
        }
        if (restaurant.getLocation() != null) {
            existing.setLocation(restaurant.getLocation());
        }
        if (restaurant.getRating() != null) {
            existing.setRating(restaurant.getRating());
        }

        Restaurant updated = restaurantRepository.save(existing);

        ResponseStructure<Restaurant> structure = new ResponseStructure<Restaurant>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurant updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteRestaurant(Long id) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurantRepository.delete(restaurant);

        ResponseStructure<String> structure = new ResponseStructure<String>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurant deleted successfully")
                .setData("Deleted restaurant with id: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByLocation(String location) {
        List<Restaurant> restaurants = restaurantRepository.findByLocationContainingIgnoreCase(location);

        ResponseStructure<List<Restaurant>> structure = new ResponseStructure<List<Restaurant>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurants fetched successfully")
                .setData(restaurants);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByName(String name) {
        List<Restaurant> restaurants = restaurantRepository.findByNameContainingIgnoreCase(name);

        ResponseStructure<List<Restaurant>> structure = new ResponseStructure<List<Restaurant>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurants fetched successfully")
                .setData(restaurants);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByRatingGreaterThan(Double rating) {
        List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThan(rating);

        ResponseStructure<List<Restaurant>> structure = new ResponseStructure<List<Restaurant>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Restaurants fetched successfully")
                .setData(restaurants);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<MenuItem>>> getMenuOfRestaurant(Long restaurantId) {
        Restaurant restaurant = findRestaurantOrThrow(restaurantId);

        ResponseStructure<List<MenuItem>> structure = new ResponseStructure<List<MenuItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Menu fetched successfully")
                .setData(restaurant.getMenuItems());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    private Restaurant findRestaurantOrThrow(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }
}
