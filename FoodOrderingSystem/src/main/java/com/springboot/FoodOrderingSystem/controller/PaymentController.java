package com.springboot.FoodOrderingSystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.Payment;
import com.springboot.FoodOrderingSystem.entity.PaymentMethod;
import com.springboot.FoodOrderingSystem.entity.PaymentStatus;
import com.springboot.FoodOrderingSystem.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ResponseStructure<Payment>> makePayment(@PathVariable Long orderId,
            @RequestParam PaymentMethod method) {
        return paymentService.makePayment(orderId, method);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Payment>> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseStructure<Payment>> getPaymentByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrder(orderId);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentByStatus(status);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByMethod(@PathVariable PaymentMethod method) {
        return paymentService.getPaymentByMethod(method);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseStructure<Payment>> updatePaymentStatus(@PathVariable Long id,
            @RequestParam PaymentStatus status) {
        return paymentService.updatePaymentStatus(id, status);
    }
}
