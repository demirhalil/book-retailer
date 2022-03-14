package com.book.service;

import com.book.domain.Customer;
import com.book.domain.Order;
import com.book.repository.CustomerRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderService orderService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, OrderService orderService) {
        this.customerRepository = customerRepository;
        this.orderService = orderService;
    }

    public Customer save(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        log.info(savedCustomer + " is saved to database successfully!");
        return savedCustomer;
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            log.info("The customer: " + customerId + " has not orders");
            return Collections.emptyList();
        }
        return orders;
    }

    public Customer getById(String customerId) {
        final Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            log.info("Customer with specified id : " + customerId + " is not found");
            return null;
        }
        return customer.get();
    }
}
