package com.springboot.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.entity.Order;
import com.springboot.FoodOrderingSystem.entity.OrderItem;
import com.springboot.FoodOrderingSystem.entity.OrderStatus;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.MenuItemRepository;
import com.springboot.FoodOrderingSystem.repository.OrderItemRepository;
import com.springboot.FoodOrderingSystem.repository.OrderRepository;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository,
            MenuItemRepository menuItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderService = orderService;
    }

    public ResponseEntity<ResponseStructure<OrderItem>> addItemToExistingOrder(Long orderId, Long menuItemId,
            Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.PREPARING) {
            throw new InvalidRequestException("Items can only be added to an order before it is prepared");
        }

        if (menuItemId == null) {
            throw new InvalidRequestException("menuItem id is required");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));

        if (!Boolean.TRUE.equals(menuItem.getAvailability())) {
            throw new InvalidRequestException("Menu item '" + menuItem.getItemName() + "' is currently unavailable");
        }

        if (quantity == null || quantity < 1) {
            throw new InvalidRequestException("Quantity must be at least 1");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setSubTotal(menuItem.getPrice() * quantity);

        order.getOrderItems().add(orderItem);
        OrderItem saved = orderItemRepository.save(orderItem);

        orderService.recalculateOrderTotal(order);

        ResponseStructure<OrderItem> structure = new ResponseStructure<OrderItem>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Item added to order successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<OrderItem>> updateItemQuantity(Long orderItemId, Integer quantity) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + orderItemId));

        Order order = orderItem.getOrder();
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.PREPARING) {
            throw new InvalidRequestException("Item quantity can only be updated before the order is prepared");
        }

        if (quantity == null || quantity < 1) {
            throw new InvalidRequestException("Quantity must be at least 1");
        }

        orderItem.setQuantity(quantity);
        orderItem.setSubTotal(orderItem.getMenuItem().getPrice() * quantity);
        OrderItem updated = orderItemRepository.save(orderItem);

        orderService.recalculateOrderTotal(order);

        ResponseStructure<OrderItem> structure = new ResponseStructure<OrderItem>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Item quantity updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> removeItemFromOrder(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + orderItemId));

        Order order = orderItem.getOrder();

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("Item cannot be removed once the order is out for delivery");
        }

        if (order.getOrderItems().size() <= 1) {
            throw new InvalidRequestException("Order must have at least 1 item; remove the item's order instead");
        }

        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);

        orderService.recalculateOrderTotal(order);

        ResponseStructure<String> structure = new ResponseStructure<String>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Item removed successfully")
                .setData("Removed order item with id: " + orderItemId);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<OrderItem>>> getAllOrderItemsOfOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);

        ResponseStructure<List<OrderItem>> structure = new ResponseStructure<List<OrderItem>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Order items fetched successfully")
                .setData(items);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}
