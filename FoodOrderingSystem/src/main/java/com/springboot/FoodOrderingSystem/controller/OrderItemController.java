package com.springboot.FoodOrderingSystem.controller;

import java.util.List;
import java.util.Map;

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
import com.springboot.FoodOrderingSystem.entity.OrderItem;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.service.OrderItemService;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ResponseStructure<OrderItem>> addItemToExistingOrder(@PathVariable Long orderId,
            @RequestBody OrderItem orderItemRequest) {
        if (orderItemRequest.getMenuItem() == null || orderItemRequest.getMenuItem().getItemId() == null) {
            throw new InvalidRequestException("menuItem id is required");
        }
        return orderItemService.addItemToExistingOrder(orderId, orderItemRequest.getMenuItem().getItemId(),
                orderItemRequest.getQuantity());
    }

    @PutMapping("/{orderItemId}/quantity")
    public ResponseEntity<ResponseStructure<OrderItem>> updateItemQuantity(@PathVariable Long orderItemId,
            @RequestBody Map<String, Integer> body) {
        return orderItemService.updateItemQuantity(orderItemId, body.get("quantity"));
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<ResponseStructure<String>> removeItemFromOrder(@PathVariable Long orderItemId) {
        return orderItemService.removeItemFromOrder(orderItemId);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseStructure<List<OrderItem>>> getAllOrderItemsOfOrder(@PathVariable Long orderId) {
        return orderItemService.getAllOrderItemsOfOrder(orderId);
    }
}
