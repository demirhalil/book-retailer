package com.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.book.domain.Customer;
import com.book.domain.Order;
import com.book.repository.CustomerRepository;
import com.book.repository.OrderRepository;
import com.book.util.TestUtil;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
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
public class CustomerIT {

    private static final String BASE_URL = "/customers";
    private static final String SECURITY_URL = "/authenticate";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @LocalServerPort
    private Integer port;
    private URI uri;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("When a valid customer is added, then it should save database and then return 201 status code")
    void testAddCustomer() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        customer = TestUtil.createCustomer("customer-name", "customer-lastname");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        // When
        ResponseEntity<Customer> response = this.restTemplate.postForEntity(uri, request, Customer.class);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1, customerRepository.findAll().size());
        assertNotNull(response.getBody().getId());
        assertEquals(customer.getName(),response.getBody().getName());
        assertEquals(customer.getLastName(),response.getBody().getLastName());

        customer = null;
        uri = null;
    }

    @Test
    @DisplayName("When a invalid customer is added, then it should not save it to database and return 400 status code")
    void testAddCustomerWhenDataIsNotValid() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        customer = TestUtil.createCustomer("", "customer-lastname");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        // When
        ResponseEntity<Customer> response = this.restTemplate.postForEntity(uri, request, Customer.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.findAll().size());

        customer = null;
        uri = null;
    }

    @Test
    @DisplayName("When a customer tries to register with the same email address then it should return 409 status code")
    void testAddCustomerWhenSameCustomerTriesToRegisterWithSameEmailAddress() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        customer = TestUtil.createCustomer("customer-name", "customer-lastname");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        // When
        this.restTemplate.postForEntity(uri, request, Customer.class);
        ResponseEntity<Customer> response = this.restTemplate.postForEntity(uri, request, Customer.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, customerRepository.findAll().size());

        customer = null;
        uri = null;
    }

    @Test
    @DisplayName("When customer has order then it should return the orders of the customer")
    void testGetOrdersByCustomerId() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        customer = TestUtil.createCustomer("customer-name", "customer-lastname");
        customerRepository.save(customer);
        Customer existCustomer = customerRepository.findAll().size() == 1 ?
            customerRepository.findAll().get(0) : null;
        assert existCustomer != null;
        Order order = new Order(null, "book-id", existCustomer.getId(), 3, LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            BigDecimal.valueOf(39));
        orderRepository.save(order);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // When
        final ResponseEntity<Order[]> response = this.restTemplate.exchange(uri + "/" + existCustomer.getId(),
            HttpMethod.GET, requestEntity, Order[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).length);
        assertEquals(existCustomer.getId(), response.getBody()[0].getCustomerId());

        uri = null;
        customer = null;
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
