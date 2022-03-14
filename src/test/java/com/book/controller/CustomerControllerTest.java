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
import com.book.service.CustomerService;
import com.book.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String BASE_URL = "/customers";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtTokenUtil;
    @MockBean
    private BookUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When a valid customer is saved then it should return 201")
    void save() throws Exception {
        // Given
        Customer customer = TestUtil.createCustomer("Jack", "London");
        String content = mapper.writeValueAsString(customer);
        Mockito.when(customerService.save(any(Customer.class))).thenReturn(customer);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(customer.getName()))
            .andExpect(jsonPath("$.lastName").value(customer.getLastName()))
            .andExpect(jsonPath("$.email").value(customer.getEmail()));
        verify(customerService, times(1)).save(any(Customer.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When an invalid customer is saved then it should return 400")
    void saveWhenInvalidCustomerIsSavedThenItShouldReturnBadRequest() throws Exception {
        // Given
        Customer customer = TestUtil.createCustomer("", "Tolstoy");
        String content = mapper.writeValueAsString(customer);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        verify(customerService, times(0)).save(any(Customer.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When customer wants to register with same email address then it should return 409")
    void saveWhenCustomerWantsToRegisterWithSameEmailAddressThenItShouldReturnConflict() throws Exception {
        // Given
        Customer customer = TestUtil.createCustomer("Yasar", "Kemal");
        String content = mapper.writeValueAsString(customer);
        Mockito.when(customerService.save(any(Customer.class))).thenThrow(DuplicateKeyException.class);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
        verify(customerService, times(1)).save(any(Customer.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    void getOrdersByCustomerId() throws Exception {
        // Given
        Book book = TestUtil.createBook("Ince Memed", "Yasar Kemal");
        Order order = TestUtil.createOrder(book, 2);
        when(customerService.getOrdersByCustomerId(anyString())).thenReturn(Arrays.asList(order));

        // When - Then
        mockMvc.perform(get(BASE_URL + "/{customerId}", "customer-id"))
            .andExpect(status().isOk());
        verify(customerService, times(1)).getOrdersByCustomerId(anyString());
    }
}