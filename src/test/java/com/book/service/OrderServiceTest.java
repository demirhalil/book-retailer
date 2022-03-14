package com.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.book.domain.Book;
import com.book.domain.Customer;
import com.book.domain.Order;
import com.book.exceptions.OrderItemNumberExceedStockException;
import com.book.repository.OrderRepository;
import com.book.util.TestUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
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
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BookService bookService;

    @Test
    @DisplayName("When an order is placed then it should saved it to database.")
    void save() {
        // Given
        Book book = TestUtil.createBook("Martin Eden", "Jack London");
        Customer customer = TestUtil.createCustomer("Halil", "Demir");
        Order order = TestUtil.createOrder(book, 5);
        when(bookService.getById(anyString())).thenReturn(book);

        // When
        final Order actualOrder = orderService.save(order);

        // Then
        Mockito.verify(bookService, Mockito.times(1)).getById(anyString());
        Mockito.verify(orderRepository, Mockito.times(1)).save(any(Order.class));
        assertEquals(book.getIsbn(), order.getBookId());
    }

    @Test
    @DisplayName("When order item number is greater than stock then it should throw RuntimeException")
    void saveWhenOrderItemNumberIsGreaterThanStockNumber() {
        // Given
        Book book = TestUtil.createBook("Homo Sapiens", "Yuval Noah Harari");
        Customer customer = TestUtil.createCustomer("Ahmet", "Cevik");
        Order order = TestUtil.createOrder(book, Integer.MAX_VALUE);
        when(bookService.getById(anyString())).thenReturn(book);

        // When
        assertThrows(OrderItemNumberExceedStockException.class, () -> orderService.save(order));
    }

    @Test
    @DisplayName("When there is an order with specified id then it should return order")
    void getById() {
        // Given
        Book book = TestUtil.createBook("Home deus", "Yuval Noah Harari");
        Customer customer = TestUtil.createCustomer("Mehmet", "Kaplan");
        Order order = TestUtil.createOrder(book, 50);
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

        // When
        Order actualOrder = orderService.getById("order-id");

        // Then
        verify(orderRepository, times(1)).findById(anyString());
        assertNotNull(actualOrder);
    }

    @Test
    @DisplayName("When there is no an order with specified id then it should return null")
    void getByIdWhenThereIsNoOrderWithSpecifiedId() {
        // Given
        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Order actualOrder = orderService.getById("order-id");

        // Then
        verify(orderRepository, times(1)).findById(anyString());
        assertNull(actualOrder);
    }

    @Test
    @DisplayName("When there are orders between given dates then it should return the orders")
    void getOrdersBetween() {
        // Given
        Book book1 = TestUtil.createBook("Marting Eden", "Jack London");
        Book book2 = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        Order order1 = TestUtil.createOrder(book1, 3);
        Order order2 = TestUtil.createOrder(book2, 10);
        when(orderRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(any(LocalDateTime.class),
            any(LocalDateTime.class))).thenReturn(
            Arrays.asList(order1, order2));

        // When
        List<Order> actualOrders = orderService.getOrdersBetween(LocalDateTime.now().minusDays(10), LocalDateTime.now());

        // Then
        verify(orderRepository, times(1)).findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(
            any(LocalDateTime.class), any(LocalDateTime.class));
        assertEquals(2, actualOrders.size());
    }

    @Test
    void getAllOrders() {
        // Given
        Book book = TestUtil.createBook("Martin Eden", "Jack London");
        Customer customer = TestUtil.createCustomer("Halil", "Demir");
        Order order = TestUtil.createOrder(book, 5);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        // When
        final List<Order> orders = orderService.getAllOrders();

        // Then
        verify(orderRepository, times(1)).findAll();
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    void getOrdersByCustomerId() {
        // Given
        Book book = TestUtil.createBook("Martin Eden", "Jack London");
        Customer customer = TestUtil.createCustomer("Halil", "Demir");
        Order order = TestUtil.createOrder(book, 5);
        when(orderRepository.findAllByCustomerId(anyString())).thenReturn(Arrays.asList(order));

        // When
        final List<Order> orders = orderService.getOrdersByCustomerId("customer-id");

        // Then
        verify(orderRepository, times(1)).findAllByCustomerId(anyString());
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }
}