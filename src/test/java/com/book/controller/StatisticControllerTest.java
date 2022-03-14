package com.book.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.book.domain.Book;
import com.book.domain.Order;
import com.book.domain.Statistic;
import com.book.security.service.BookUserDetailsService;
import com.book.security.util.JwtUtil;
import com.book.service.StatisticService;
import com.book.util.TestUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StatisticController.class)
class StatisticControllerTest {

    private static final String BASE_URL = "/statistics";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatisticService statisticService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtTokenUtil;
    @MockBean
    private BookUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    void getStatistics() throws Exception {
        // Given
        Book martingEden = TestUtil.createBook("Marting Eden", "Jack London");
        Book crimeAndPunishment = TestUtil.createBook("Crime and Punishment", "Dostoyevski");
        Order order1 = TestUtil.createOrder(martingEden, 2);
        Order order2 = TestUtil.createOrder(crimeAndPunishment, 1);
        Map<String, Map<String, Statistic>> statistic = new HashMap<>();
        Map<String, Statistic> statisticForCustomer1 = new HashMap<>();
        statisticForCustomer1.put(LocalDateTime.now().getMonth().toString(), Statistic.of(1, order1.getItemNumber(), order1.getAmount()));
        Map<String, Statistic> statisticForCustomer2 = new HashMap<>();
        statisticForCustomer1.put(LocalDateTime.now().getMonth().toString(), Statistic.of(1, order2.getItemNumber(), order2.getAmount()));
        statistic.put(order1.getCustomerId(), statisticForCustomer1);
        statistic.put(order2.getCustomerId(), statisticForCustomer2);
        Mockito.when(statisticService.getStatistics()).thenReturn(statistic);

        //When - Then
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(2)));
        Mockito.verify(statisticService, Mockito.times(1)).getStatistics();
    }
}