package com.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.book.domain.Book;
import com.book.repository.BookRepository;
import com.book.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;

    @Test
    @DisplayName("When a valid book is given then it should return the saved book.")
    void save() {
        // Given
        Book book = TestUtil.createBook("Grimm Stories", "Grimm Brothers");
        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        Book actualBook = bookService.save(book);

        // Then
        verify(bookRepository, times(1)).save(any(Book.class));
        assertEquals(book.getIsbn(), actualBook.getIsbn());
        assertEquals(book.getName(), actualBook.getName());
        assertEquals(book.getAuthor(), actualBook.getAuthor());
        assertEquals(book.getStock(), actualBook.getStock());
        assertEquals(book.getPrice().intValue(), actualBook.getPrice().intValue());
    }

    @Test
    @DisplayName("When stock is updated then it should return updated book.")
    void updateStock() {
        // Given
        Book book = TestUtil.createBook("Crime and Punishment", "Fyodor Dostoevsky");
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        // When
        Book actualBook = bookService.updateStock(book.getIsbn(), 12);

        // Then
        verify(bookRepository, times(1)).findById(anyString());
        verify(bookRepository, times(1)).save(any(Book.class));
        assertEquals(book.getAuthor(), actualBook.getAuthor());
        assertEquals(book.getPrice().intValue(), actualBook.getPrice().intValue());
    }

    @Test
    @DisplayName("When stock is updated but there is no book specified book then it should return null")
    void updateStockWhenThereIsNoBookWithSpecifiedIsbn() {
        // Given
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Book actualBook = bookService.updateStock("book-id", 12);

        // Then
        verify(bookRepository, times(1)).findById(anyString());
        verify(bookRepository, times(0)).save(any(Book.class));
        assertEquals(null, actualBook);
    }

    @Test
    @DisplayName("When given isbn does not exists then it should return null")
    void getByIsbnWhenIsbnIsNotExists() {
        // Given
        Book book = TestUtil.createBook("Crime and Punishment", "Fyodor Dostoevsky");
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Book actualBook = bookService.getById(book.getIsbn());

        // Then
        verify(bookRepository, times(1)).findById(anyString());
        assertEquals(null,actualBook);
    }

    @Test
    @DisplayName("When given isbn is exists then it should return the book")
    void getByIsbn() {
        // Given
        Book book = TestUtil.createBook("Crime and Punishment", "Fyodor Dostoevsky");
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        // When
        Book actualBook = bookService.getById(book.getIsbn());

        // Then
        verify(bookRepository, times(1)).findById(anyString());
        assertEquals(book,actualBook);
    }
}