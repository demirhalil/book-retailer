package com.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.book.domain.Book;
import com.book.repository.BookRepository;
import com.book.util.TestUtil;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookITTest {

    private static final String BASE_URL = "/books";
    private static final String SECURITY_URL = "/authenticate";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private BookRepository bookRepository;
    @LocalServerPort
    private Integer port;
    private URI uri;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("When a valid book is given then it should saved it to database and return 201 status code")
    void testAddBook() throws URISyntaxException {
        // Given
        book = TestUtil.createBook("Martin Eden", "Jack London");
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Book> request = new HttpEntity<>(book, headers);

        // When
        ResponseEntity<Book> response = this.restTemplate.postForEntity(uri, request, Book.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
        assertEquals(book.getName(), response.getBody().getName());
        assertEquals(book.getAuthor(), response.getBody().getAuthor());
        assertEquals(book.getIsbn(), response.getBody().getIsbn());
        assertEquals(book.getPrice(), response.getBody().getPrice());
        assertEquals(book.getStock(), response.getBody().getStock());
        assertEquals(1, bookRepository.findAll().size());
    }

    @Test
    @DisplayName("When a book is given without isbn number then it should not save it to database and return 400 status code")
    void testAddBookWhenIsbnNumberIsMissing() throws URISyntaxException {
        // Given
        book = TestUtil.createBook("Martin Eden", "Jack London");
        book.setIsbn(null);
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<Book> request = new HttpEntity<>(book, headers);

        // When
        ResponseEntity<Book> response = this.restTemplate.postForEntity(uri, request, Book.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, bookRepository.findAll().size());
    }

    @Test
    @DisplayName("When stock of book is updated then it should updated and then return 200 status code")
    void testUpdateStock() throws URISyntaxException {
        // Given
        book = TestUtil.createBook("Martin Eden", "Jack London");
        book.setStock(20);
        bookRepository.save(book);
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        uri = URI.create(uri + "?id=" + bookRepository.findAll().get(0).getId() + "&stock=12");

        // When
        this.restTemplate.put(uri, request);

        // Then
        assertEquals(1, bookRepository.findAll().size());
        assertEquals(12, bookRepository.findAll().get(0).getStock());
    }

    @Test
    @DisplayName("When stock of book is updated but the book does not exist then it should  return 400 status code")
    void testUpdateStockWhenBookDoesNotExist() throws URISyntaxException {
        // Given
        uri = new URI(BASE_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getBearerToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        uri = URI.create(uri + "?id=" + "not-exist-book-id" + "&stock=12");

        // When
        this.restTemplate.put(uri, request);

        // Then
        assertEquals(0, bookRepository.findAll().size());
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
