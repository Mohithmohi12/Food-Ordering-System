package com.springboot.FoodOrderingSystem.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.Order;
import com.springboot.FoodOrderingSystem.entity.OrderStatus;
import com.springboot.FoodOrderingSystem.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseStructure<Order>> placeOrder(@RequestBody Order orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<Order>>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResponseStructure<List<Order>>> getAllOrdersOfCustomer(@PathVariable Long customerId) {
        return orderService.getAllOrdersOfCustomer(customerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Order>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseStructure<Order>> updateOrderStatus(@PathVariable Long id,
            @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ResponseStructure<Order>> cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return orderService.getOrdersByDate(date);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersPlacedInRestaurant(
            @PathVariable Long restaurantId) {
        return orderService.getOrdersPlacedInRestaurant(restaurantId);
    }
}
