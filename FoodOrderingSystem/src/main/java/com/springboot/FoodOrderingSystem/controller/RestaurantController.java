package com.springboot.FoodOrderingSystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.entity.Restaurant;
import com.springboot.FoodOrderingSystem.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<ResponseStructure<Restaurant>> addRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Restaurant>> updateRestaurant(@PathVariable Long id,
            @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(id, restaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteRestaurant(@PathVariable Long id) {
        return restaurantService.deleteRestaurant(id);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByLocation(
            @PathVariable String location) {
        return restaurantService.getRestaurantByLocation(location);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByName(@PathVariable String name) {
        return restaurantService.getRestaurantByName(name);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantByRatingGreaterThan(
            @PathVariable Double rating) {
        return restaurantService.getRestaurantByRatingGreaterThan(rating);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<ResponseStructure<List<MenuItem>>> getMenuOfRestaurant(@PathVariable Long id) {
        return restaurantService.getMenuOfRestaurant(id);
    }
}
