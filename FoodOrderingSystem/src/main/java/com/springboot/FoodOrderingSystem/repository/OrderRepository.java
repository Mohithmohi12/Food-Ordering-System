package com.springboot.FoodOrderingSystem.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.FoodOrderingSystem.entity.Order;
import com.springboot.FoodOrderingSystem.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByOrderDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN oi.menuItem mi WHERE mi.restaurant.id = :restaurantId")
    List<Order> findOrdersPlacedInRestaurant(@Param("restaurantId") Long restaurantId);
}
