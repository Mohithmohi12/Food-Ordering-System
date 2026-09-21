package com.springboot.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.Customer;
import com.springboot.FoodOrderingSystem.entity.OrderStatus;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.exception.ResourceNotFoundException;
import com.springboot.FoodOrderingSystem.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<ResponseStructure<Customer>> createCustomer(Customer customer) {
        validateContact(customer.getContact());
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new InvalidRequestException("Email already registered: " + customer.getEmail());
        }
        if (customerRepository.existsByContact(customer.getContact())) {
            throw new InvalidRequestException("Contact number already registered: " + customer.getContact());
        }

        Customer saved = customerRepository.save(customer);

        ResponseStructure<Customer> structure = new ResponseStructure<Customer>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("Customer created successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        ResponseStructure<List<Customer>> structure = new ResponseStructure<List<Customer>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customers fetched successfully")
                .setData(customers);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Customer>> getCustomerById(Long id) {
        Customer customer = findCustomerOrThrow(id);

        ResponseStructure<Customer> structure = new ResponseStructure<Customer>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customer fetched successfully")
                .setData(customer);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Customer>> updateCustomer(Long id, Customer customer) {
        Customer existing = findCustomerOrThrow(id);

        if (customer.getEmail() != null && !customer.getEmail().equals(existing.getEmail())
                && customerRepository.existsByEmail(customer.getEmail())) {
            throw new InvalidRequestException("Email already registered: " + customer.getEmail());
        }
        if (customer.getContact() != null && !customer.getContact().equals(existing.getContact())) {
            validateContact(customer.getContact());
            if (customerRepository.existsByContact(customer.getContact())) {
                throw new InvalidRequestException("Contact number already registered: " + customer.getContact());
            }
        }

        if (customer.getName() != null) {
            existing.setName(customer.getName());
        }
        if (customer.getEmail() != null) {
            existing.setEmail(customer.getEmail());
        }
        if (customer.getContact() != null) {
            existing.setContact(customer.getContact());
        }
        if (customer.getAddress() != null) {
            existing.setAddress(customer.getAddress());
        }

        Customer updated = customerRepository.save(existing);

        ResponseStructure<Customer> structure = new ResponseStructure<Customer>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customer updated successfully")
                .setData(updated);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteCustomer(Long id) {
        Customer customer = findCustomerOrThrow(id);

        boolean hasIncompleteOrders = customer.getOrders().stream()
                .anyMatch(order -> order.getStatus() != OrderStatus.DELIVERED
                        && order.getStatus() != OrderStatus.CANCELLED);

        if (hasIncompleteOrders) {
            throw new InvalidRequestException(
                    "Customer can only be deleted once all their orders have been completed");
        }

        customerRepository.delete(customer);

        ResponseStructure<String> structure = new ResponseStructure<String>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customer deleted successfully")
                .setData("Deleted customer with id: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Customer>> getCustomerByContact(String contact) {
        Customer customer = customerRepository.findByContact(contact)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with contact: " + contact));

        ResponseStructure<Customer> structure = new ResponseStructure<Customer>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customer fetched successfully")
                .setData(customer);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Customer>> getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        ResponseStructure<Customer> structure = new ResponseStructure<Customer>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customer fetched successfully")
                .setData(customer);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<List<Customer>>> getCustomerByName(String name) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name);

        ResponseStructure<List<Customer>> structure = new ResponseStructure<List<Customer>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Customers fetched successfully")
                .setData(customers);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private void validateContact(String contact) {
        if (contact == null || !contact.matches("\\d{10}")) {
            throw new InvalidRequestException("Contact number must be exactly 10 digits");
        }
    }
}
