package com.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.book.domain.Book;
import com.book.domain.Customer;
import com.book.domain.Order;
import com.book.repository.CustomerRepository;
import com.book.util.TestUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderService orderService;

    @Test
    @DisplayName("When a customer is saved it should return saved customer")
    void save() {
        // Given
        Customer customer = TestUtil.createCustomer("Ali", "Yilmaz");
        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        // When
        Customer actualCustomer = customerService.save(customer);

        // Then
        Mockito.verify(customerRepository, times(1)).save(any(Customer.class));
        assertEquals(customer.getName(),actualCustomer.getName());
        assertEquals(customer.getLastName(),actualCustomer.getLastName());
        assertEquals(customer.getEmail(),actualCustomer.getEmail());
    }

    @Test
    @DisplayName("When a customer is requested by id then it should return the customer")
    void getByEmailAddress() {
        // Given
        final String customerId = "customer-id";
        Customer customer = TestUtil.createCustomer("Inan", "Kinu");
        Mockito.when(customerRepository.findById(anyString())).thenReturn(Optional.of(customer));

        // When
        Customer actualCustomer = customerService.getById(customerId);

        // Then
        Mockito.verify(customerRepository, times(1)).findById(anyString());
        assertEquals(customer.getName(),actualCustomer.getName());
        assertEquals(customer.getLastName(),actualCustomer.getLastName());
        assertEquals(customer.getEmail(),actualCustomer.getEmail());
    }

    @Test
    @DisplayName("When a customer is requested by id but no customer then it should return null")
    void getByEmailAddressWhenThereIsNoCustomer() {
        // Given
        final String customerId = "customer-id";
        Mockito.when(customerRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Customer expectedCustomer = customerService.getById(customerId);

        // Then
        Mockito.verify(customerRepository, times(1)).findById(anyString());
        assertEquals(null, expectedCustomer);
    }

    @Test
    @DisplayName("When there are orders for specified customer then it should return orders")
    void getOrdersByCustomerId() {
        // Given
        Book book = TestUtil.createBook("Home deus", "Yuval Noah Harari");
        Customer customer = TestUtil.createCustomer("Mehmet", "Kaplan");
        Order order = TestUtil.createOrder(book, 50);
        when(orderService.getOrdersByCustomerId(anyString())).thenReturn(Arrays.asList(order));

        // When
        final List<Order> orders = customerService.getOrdersByCustomerId("customer-id");

        // Then
        verify(orderService, times(1)).getOrdersByCustomerId(anyString());
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order.getCustomerId(), orders.get(0).getCustomerId());
    }

    @Test
    @DisplayName("When there are no orders for specified customer then it should return empty list")
    void getOrdersByCustomerIdWhenThereAreNoOrders() {
        // Given
        when(orderService.getOrdersByCustomerId(anyString())).thenReturn(Collections.emptyList());

        // When
        final List<Order> orders = customerService.getOrdersByCustomerId("customer-id");

        // Then
        verify(orderService, times(1)).getOrdersByCustomerId(anyString());
        assertEquals(0,orders.size());
    }
}