package com.springboot.FoodOrderingSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.Customer;
import com.springboot.FoodOrderingSystem.entity.MenuItem;
import com.springboot.FoodOrderingSystem.entity.Order;
import com.springboot.FoodOrderingSystem.entity.OrderItem;
import com.springboot.FoodOrderingSystem.entity.OrderStatus;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.CustomerRepository;
import com.springboot.FoodOrderingSystem.repository.MenuItemRepository;
import com.springboot.FoodOrderingSystem.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
            MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public ResponseEntity<ResponseStructure<Order>> placeOrder(Order orderRequest) {
        if (orderRequest.getCustomer() == null || orderRequest.getCustomer().getId() == null) {
            throw new InvalidRequestException("customer id is required to place an order");
        }
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new InvalidRequestException("Order must have at least 1 item");
        }

        Customer customer = customerRepository.findById(orderRequest.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.getCustomer().getId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PLACED);
        order.setOrderItems(new ArrayList<>());

        for (OrderItem itemRequest : orderRequest.getOrderItems()) {
            if (itemRequest.getMenuItem() == null || itemRequest.getMenuItem().getItemId() == null) {
                throw new InvalidRequestException("menuItem id is required for every order item");
            }
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() < 1) {
                throw new InvalidRequestException("quantity must be at least 1 for every order item");
            }

            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItem().getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item not found with id: " + itemRequest.getMenuItem().getItemId()));

            if (!Boolean.TRUE.equals(menuItem.getAvailability())) {
                throw new InvalidRequestException(
                        "Menu item '" + menuItem.getItemName() + "' is currently unavailable");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubTotal(menuItem.getPrice() * itemRequest.getQuantity());
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(calculateTotal(order));
        Order saved = orderRepository.save(order);

        ResponseStructure<Order> structure = new ResponseStructure<Order>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Order placed successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<List<Order>>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        ResponseStructure<List<Order>> structure = new ResponseStructure<List<Order>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Orders fetched successfully")
                .setData(orders);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Order>>> getAllOrdersOfCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        ResponseStructure<List<Order>> structure = new ResponseStructure<List<Order>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Orders fetched successfully")
                .setData(orders);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Order>> getOrderById(Long id) {
        Order order = findOrderOrThrow(id);

        ResponseStructure<Order> structure = new ResponseStructure<Order>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Order fetched successfully")
                .setData(order);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Order>> updateOrderStatus(Long id, OrderStatus status) {
        if (status == null) {
            throw new InvalidRequestException("status is required");
        }
        Order order = findOrderOrThrow(id);
        order.setStatus(status);
        Order updated = orderRepository.save(order);

        ResponseStructure<Order> structure = new ResponseStructure<Order>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Order status updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Order>> cancelOrder(Long id) {
        Order order = findOrderOrThrow(id);

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new InvalidRequestException("Order cannot be cancelled after preparation has started");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);

        ResponseStructure<Order> structure = new ResponseStructure<Order>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Order cancelled successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);

        ResponseStructure<List<Order>> structure = new ResponseStructure<List<Order>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Orders fetched successfully")
                .setData(orders);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderDateTimeBetween(start, end);

        ResponseStructure<List<Order>> structure = new ResponseStructure<List<Order>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Orders fetched successfully")
                .setData(orders);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Order>>> getOrdersPlacedInRestaurant(Long restaurantId) {
        List<Order> orders = orderRepository.findOrdersPlacedInRestaurant(restaurantId);

        ResponseStructure<List<Order>> structure = new ResponseStructure<List<Order>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Orders fetched successfully")
                .setData(orders);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    /**
     * Internal helper used by OrderItemService after items are added, updated, or removed.
     * Not exposed as an endpoint, so it returns the entity directly rather than a ResponseStructure.
     */
    public void recalculateOrderTotal(Order order) {
        order.setTotalAmount(calculateTotal(order));
        orderRepository.save(order);
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private Double calculateTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }
}
