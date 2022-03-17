package com.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.book.domain.Book;
import com.book.domain.Order;
import com.book.domain.Statistic;
import com.book.repository.BookRepository;
import com.book.repository.OrderRepository;
import com.book.util.TestUtil;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StatisticITTest {

    private static final String BASE_URL = "/statistics";
    private static final String SECURITY_URL = "/authenticate";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void testGetStatistics() throws URISyntaxException {
        // Given
        URI uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        createOrders();

        // When
        ResponseEntity<?> response =
            this.restTemplate.exchange(uri, HttpMethod.GET,request, Object.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Map<String, Statistic>> result = (Map<String, Map<String, Statistic>>) response.getBody();
        assertEquals(2, result.size());
    }

    private void createOrders() {
        Book book1 = TestUtil.createBook("Marting Eden", "Jack London");
        book1.setStock(20);
        bookRepository.save(book1);
        BigDecimal amount = book1.getPrice().multiply(BigDecimal.valueOf(5L));
        Order order1 = new Order(null,bookRepository.findAll().get(0).getId(),"test_customer_1",1,
            LocalDateTime.now(),LocalDateTime.now().plusDays(1), amount);
        orderRepository.save(order1);

        Book book2 = TestUtil.createBook("Marting Eden", "Jack London");
        book2.setStock(20);
        bookRepository.save(book2);
        BigDecimal amount2 = book2.getPrice().multiply(BigDecimal.valueOf(5L));
        Order order2 = new Order(null,bookRepository.findAll().get(0).getId(),"test_customer_2",12,
            LocalDateTime.now(),LocalDateTime.now().plusDays(6), amount);
        orderRepository.save(order2);
    }

    private String getBearerToken() throws URISyntaxException {
        String body = "{\"username\":\"foo\",\"password\":\"foo\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body,headers);
        final ResponseEntity<String> response = this.restTemplate.postForEntity(new URI(SECURITY_URL),
            request, String.class);
        return response.getBody();
    }

}
