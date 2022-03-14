package com.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.book.domain.Book;
import com.book.domain.Order;
import com.book.domain.Statistic;
import com.book.util.TestUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

    @InjectMocks
    private StatisticService statisticService;
    @Mock
    private OrderService orderService;

    @Test
    @DisplayName("When the customers are unique then it should return statistic for each customer")
    void getStatistics() {
        // Given
        Book martingEden = TestUtil.createBook("Marting Eden", "Jack London");
        Book crimeAndPunishment = TestUtil.createBook("Crime and Punishment", "Dostoyevski");
        Order order1 = TestUtil.createOrder(martingEden, 2);
        Order order2 = TestUtil.createOrder(crimeAndPunishment, 1);
        Mockito.when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        // When
        Map<String, Map<String, Statistic>> result = statisticService.getStatistics();

        // Then
        Statistic statisticOfCustomer1 = result.get(order1.getCustomerId()).get(LocalDateTime.now().getMonth().toString());
        Statistic statisticOfCustomer2 = result.get(order2.getCustomerId()).get(LocalDateTime.now().getMonth().toString());

        Mockito.verify(orderService, Mockito.times(1)).getAllOrders();

        assertNotEquals(null, result);
        assertEquals(2, result.size());

        assertEquals(order1.getAmount(), statisticOfCustomer1.getTotalPurchasedAmount());
        assertEquals(order1.getItemNumber(),statisticOfCustomer1.getTotalBookCount());
        assertEquals(1, statisticOfCustomer1.getTotalOrderCount());

        assertEquals(order2.getAmount(), statisticOfCustomer2.getTotalPurchasedAmount());
        assertEquals(order2.getItemNumber(),statisticOfCustomer2.getTotalBookCount());
        assertEquals(1, statisticOfCustomer2.getTotalOrderCount());
    }

    @Test
    @DisplayName("When there is only one customer then it should return statistic for that customer")
    void getStatisticsForSameCustomer() {
        // Given
        Book martingEden = TestUtil.createBook("Marting Eden", "Jack London");
        Book crimeAndPunishment = TestUtil.createBook("Crime and Punishment", "Dostoyevski");
        Order order1 = TestUtil.createOrder(martingEden, 2);
        Order order2 = TestUtil.createOrder(crimeAndPunishment, 1);
        order2.setCustomerId(order1.getCustomerId());
        Mockito.when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        // When
        Map<String, Map<String, Statistic>> result = statisticService.getStatistics();

        // Then
        Statistic statisticOfCustomer = result.get(order1.getCustomerId()).get(LocalDateTime.now().getMonth().toString());

        Mockito.verify(orderService, Mockito.times(1)).getAllOrders();

        assertNotEquals(null, result);
        assertEquals(1, result.size());

        int totalBookCount = order1.getItemNumber() + order2.getItemNumber();
        BigDecimal totalAmount = order1.getAmount().add(order2.getAmount());
        assertEquals(totalAmount, statisticOfCustomer.getTotalPurchasedAmount());
        assertEquals(totalBookCount,statisticOfCustomer.getTotalBookCount());
        assertEquals(2, statisticOfCustomer.getTotalOrderCount());
    }
}