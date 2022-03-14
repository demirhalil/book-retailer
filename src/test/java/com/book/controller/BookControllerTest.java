package com.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.book.domain.Book;
import com.book.security.service.BookUserDetailsService;
import com.book.security.util.JwtUtil;
import com.book.service.BookService;
import com.book.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@WebMvcTest(value = BookController.class)
class BookControllerTest {

    private static final String BASE_URL = "/books";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtTokenUtil;
    @MockBean
    private BookUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When a valid book is given then it should return 201")
    void save() throws Exception {
        // Given
        Book book = TestUtil.createBook("White Nights", "Fyodor Dostoevsky");
        String content = mapper.writeValueAsString(book);
        Mockito.when(bookService.save(any(Book.class))).thenReturn(book);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
            .andExpect(jsonPath("$.name").value(book.getName()))
            .andExpect(jsonPath("$.author").value(book.getAuthor()))
            .andExpect(jsonPath("$.stock").value(book.getStock()));
        verify(bookService, times(1)).save(any(Book.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When an invalid book is given then it should return 400")
    void saveWhenInvalidBookIsSavedThenItShouldReturnBadRequest() throws Exception {
        // Given
        Book book = TestUtil.createBook("", "Fyodor Dostoevsky");
        String content = mapper.writeValueAsString(book);
        Mockito.when(bookService.save(any(Book.class))).thenReturn(book);

        // When - Then
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        verify(bookService, times(0)).save(any(Book.class));
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When stock is updated then it should return 200")
    void updateStock() throws Exception {
        // Given
        Book book = TestUtil.createBook("White Fang", "Jack London");
        Book updatedBook = book;
        updatedBook.setStock(12);
        bookService.save(book);
        Mockito.when(bookService.updateStock(anyString(),anyInt())).thenReturn(updatedBook);

        // When - Then
        mockMvc.perform(put(BASE_URL)
                .param("id","book-id")
                .param("stock","12"))
            .andExpect(status().isOk());
        verify(bookService, times(1)).updateStock(anyString(), anyInt());
    }

    @Test
    @WithMockUser(username = TestUtil.MOCK_USER_NAME, password = TestUtil.MOCK_USER_NAME)
    @DisplayName("When stock is updated but given isbn is wrong then it should return 404")
    void updateStockWhenGivenIsbnIsWrong() throws Exception {
        // Given
        Book book = TestUtil.createBook("The Call of the Wild", "Jack London");
        bookService.save(book);
        Mockito.when(bookService.updateStock(anyString(),anyInt())).thenReturn(null);

        // When - Then
        mockMvc.perform(put(BASE_URL)
                .param("id","book-id")
                .param("stock","12"))
            .andExpect(status().isNotFound());
        verify(bookService, times(1)).updateStock(anyString(), anyInt());
    }
}
