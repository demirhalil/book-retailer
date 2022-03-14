package com.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.book.domain.Book;
import com.book.domain.Customer;
import com.book.domain.Order;
import com.book.security.service.BookUserDetailsService;
import com.book.security.util.JwtUtil;
import com.book.service.OrderService;
import com.book.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private static final String BASE_URL = "/orders";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtTokenUtil;
    @MockBean
    private BookUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When an valid order is given then it should return 201 status code")
    void save() throws Exception{
        // Given
        mapper.registerModule(new JavaTimeModule());
        Customer customer = TestUtil.createCustomer("Hilal", "Aslan");
        Book book = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        Order order = TestUtil.createOrder(book, 2);
        String content = mapper.writeValueAsString(order);
        String emailAddress = customer.getEmail();
        Mockito.when(orderService.save(any(Order.class))).thenReturn(order);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bookId").value(book.getIsbn()));
        verify(orderService, times(1)).save(any(Order.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When order is missing isbn number then it should return 404 status code")
    void saveWhenOrderIsMissingIsbnNumber() throws Exception{
        // Given
        mapper.registerModule(new JavaTimeModule());
        Customer customer = TestUtil.createCustomer("Hilal", "Aslan");
        Book book = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        book.setIsbn("");
        Order order = TestUtil.createOrder(book, 2);
        String content = mapper.writeValueAsString(order);
        String emailAddress = customer.getEmail();
        Mockito.when(orderService.save(any(Order.class))).thenReturn(order);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        verify(orderService, times(0)).save(any(Order.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When given id exits then it should return 200 status code")
    void getById() throws Exception{
        // Given
        Book book = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        Order order = TestUtil.createOrder(book, 2);
        when(orderService.getById(anyString())).thenReturn(order);

        // When - Then
        mockMvc.perform(get(BASE_URL + "/id", "sample-id"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookId").exists())
            .andExpect(jsonPath("$.itemNumber").value(2));
        verify(orderService, times(1)).getById(anyString());
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When given id exits then it should return 200 status code")
    void getByIdWhenIdDoesNotExist() throws Exception{
        // Given
        when(orderService.getById(anyString())).thenReturn(null);

        // When - Then
        mockMvc.perform(get(BASE_URL + "/id", "sample-id"))
            .andExpect(status().isNotFound());
        verify(orderService, times(1)).getById(anyString());
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When there are orders between given dates then it should return orders and 200 status code")
    void getOrdersBetween() throws Exception {
        // Given
        Book book1 = TestUtil.createBook("Marting Eden", "Jack London");
        Book book2 = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        Order order1 = TestUtil.createOrder(book1, 3);
        Order order2 = TestUtil.createOrder(book2, 10);
        when(orderService.getOrdersBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(
            Arrays.asList(order1, order2));

        // When - Then
        mockMvc.perform(get(BASE_URL)
                .param("from", String.valueOf(LocalDateTime.now().minusDays(10)))
                .param("to", String.valueOf(LocalDateTime.now())))
            .andExpect(status().isOk());
        verify(orderService, times(1)).getOrdersBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}