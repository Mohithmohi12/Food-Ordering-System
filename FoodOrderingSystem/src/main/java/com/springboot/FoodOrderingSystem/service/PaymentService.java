package com.springboot.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.Order;
import com.springboot.FoodOrderingSystem.entity.Payment;
import com.springboot.FoodOrderingSystem.entity.PaymentMethod;
import com.springboot.FoodOrderingSystem.entity.PaymentStatus;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.OrderRepository;
import com.springboot.FoodOrderingSystem.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public ResponseEntity<ResponseStructure<Payment>> makePayment(Long orderId, PaymentMethod paymentMethod) {
        if (orderId == null) {
            throw new InvalidRequestException("orderId is required");
        }
        if (paymentMethod == null) {
            throw new InvalidRequestException("paymentMethod is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getPayment() != null) {
            throw new InvalidRequestException("Payment has already been made for order id: " + order.getOrderId());
        }

        // Order total amount must be the same as the payment amount
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        order.setPayment(payment);
        Payment saved = paymentRepository.save(payment);

        ResponseStructure<Payment> structure = new ResponseStructure<Payment>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Payment made successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<Payment>> getPaymentById(Long id) {
        Payment payment = findPaymentOrThrow(id);

        ResponseStructure<Payment> structure = new ResponseStructure<Payment>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Payment fetched successfully")
                .setData(payment);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Payment>> getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        ResponseStructure<Payment> structure = new ResponseStructure<Payment>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Payment fetched successfully")
                .setData(payment);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByPaymentStatus(status);

        ResponseStructure<List<Payment>> structure = new ResponseStructure<List<Payment>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Payments fetched successfully")
                .setData(payments);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByMethod(PaymentMethod method) {
        List<Payment> payments = paymentRepository.findByPaymentMethod(method);

        ResponseStructure<List<Payment>> structure = new ResponseStructure<List<Payment>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Payments fetched successfully")
                .setData(payments);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Payment>> updatePaymentStatus(Long id, PaymentStatus status) {
        if (status == null) {
            throw new InvalidRequestException("paymentStatus is required");
        }
        Payment payment = findPaymentOrThrow(id);
        payment.setPaymentStatus(status);
        Payment updated = paymentRepository.save(payment);

        ResponseStructure<Payment> structure = new ResponseStructure<Payment>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Payment status updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    private Payment findPaymentOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }
}
