package com.book.service;

import com.book.domain.Book;
import com.book.domain.Order;
import com.book.exceptions.OrderItemNumberExceedStockException;
import com.book.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;

    @Autowired
    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    public Order save(Order order) {
        BigDecimal amount;
        synchronized (this) {
            Book book = bookService.getById(order.getBookId());
            if (book.getStock() < order.getItemNumber()) {
                throw new OrderItemNumberExceedStockException(
                    "Order item number cannot be greater than number of item in the stock. Stock number is: "
                        + book.getStock() + " Order item number is: " + order.getItemNumber());
            }
            int newStock = book.getStock() - order.getItemNumber();
            book.setStock(newStock);
            bookService.save(book);
            log.info("Stock is updated for the book: " + book);
            amount = book.getPrice().multiply(BigDecimal.valueOf(order.getItemNumber()));
        }
        order.setAmount(amount);
        final Order savedOrder = orderRepository.save(order);
        log.info(savedOrder + " is saved successfully!");
        return savedOrder;
    }

    public Order getById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            log.warn("There is no order for specified id: " + id);
            return null;
        }
        return order.get();
    }

    public List<Order> getOrdersBetween(LocalDateTime from, LocalDateTime to) {
        return orderRepository.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(from, to);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }
}
