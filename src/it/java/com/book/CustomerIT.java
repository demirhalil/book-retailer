package com.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.book.domain.Customer;
import com.book.repository.CustomerRepository;
import com.book.util.TestUtil;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void testAddCustomer() throws URISyntaxException {
        // Given
        URI uri = new URI(BASE_URL);
        Customer customer = TestUtil.createCustomer("customer-name", "customer-lastname");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        // When
        ResponseEntity<Customer> result = this.restTemplate.postForEntity(uri, request, Customer.class);

        // Then
        assertEquals(201, result.getStatusCodeValue());
        assertEquals(1, customerRepository.findAll().size());
        assertNotNull(result.getBody().getId());
        assertEquals(customer.getName(),result.getBody().getName());
        assertEquals(customer.getLastName(),result.getBody().getLastName());
    }

    private String getBearerToken() throws URISyntaxException {
        String body = "{\"username\":\"foo\",\"password\":\"foo\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body,headers);
        final ResponseEntity<String> stringResponseEntity = this.restTemplate.postForEntity(new URI(SECURITY_URL),
            request, String.class);
        return stringResponseEntity.getBody().split(":")[1].split("[},\"]")[1];
    }
}
