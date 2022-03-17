package com.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.book.domain.Book;
import com.book.domain.Order;
import com.book.repository.BookRepository;
import com.book.repository.OrderRepository;
import com.book.util.TestUtil;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderITTest {

    private static final String BASE_URL = "/orders";
    private static final String SECURITY_URL = "/authenticate";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BookRepository bookRepository;
    @LocalServerPort
    private Integer port;
    private URI uri;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("When order is added then it should save it and return 201 status code")
    void testAddOrder() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        Book book = TestUtil.createBook("Marting Eden", "Jack London");
        book.setStock(20);
        bookRepository.save(book);
        BigDecimal amount = book.getPrice().multiply(BigDecimal.valueOf(5L));
        Order order = new Order(null,bookRepository.findAll().get(0).getId(),"test_customer",5,
            LocalDateTime.now(),LocalDateTime.now().plusDays(15), amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        // When
        ResponseEntity<Order> response = this.restTemplate.postForEntity(uri, request, Order.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, orderRepository.findAll().size());
        assertEquals(15, bookRepository.findAll().get(0).getStock());
    }

    @Test
    @DisplayName("When order is added but bookId is missing then it should return 400 status code")
    void testAddOrderWhenBookIdIsMissing() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        Book book = TestUtil.createBook("Marting Eden", "Jack London");
        book.setStock(20);
        bookRepository.save(book);
        BigDecimal amount = book.getPrice().multiply(BigDecimal.valueOf(5L));
        String emptyBookId = "";
        Order order = new Order(null,emptyBookId,"test_customer",5,
            LocalDateTime.now(),LocalDateTime.now().plusDays(15), amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        // When
        ResponseEntity<Order> response = this.restTemplate.postForEntity(uri, request, Order.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, orderRepository.findAll().size());
        assertEquals(20, book.getStock());
    }

    @Test
    @DisplayName("When order is requested by id then it should return the order and return 200 status code")
    void testGetById() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        Book book = TestUtil.createBook("Marting Eden", "Jack London");
        book.setStock(20);
        bookRepository.save(book);
        BigDecimal amount = book.getPrice().multiply(BigDecimal.valueOf(5L));
        Order order = new Order(null,bookRepository.findAll().get(0).getId(),"test_customer",5,
            LocalDateTime.now(),LocalDateTime.now().plusDays(15), amount);
        orderRepository.save(order);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<Order> response = this.restTemplate.exchange(uri + "/" +orderRepository.findAll().get(0).getId(),
            HttpMethod.GET, request, Order.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("When order is requested by id but when it does not exist then it should return 404 status code")
    void testGetByIdWhenOrderDoesNotExist() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String notExistOrderId = "not-exist";

        // When
        ResponseEntity<Order> response = this.restTemplate.exchange(uri + "/" + notExistOrderId,
            HttpMethod.GET, request, Order.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("When orders are requested between 2 dates then it should return orders and return 200 status code")
    void testGetOrdersBetween() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        createOrders();
        String from = LocalDateTime.now().minusDays(20).toString();
        String to = LocalDateTime.now().plusDays(10).toString();
        uri = URI.create(uri + "?from=" + from + "&to=" + to);

        // When
        ResponseEntity<Order[]> response = this.restTemplate.exchange(uri,
            HttpMethod.GET, request, Order[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
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
