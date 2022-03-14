package com.book.service;

import com.book.domain.Book;
import com.book.repository.BookRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book save(Book book) {
        Book savedBook = bookRepository.save(book);
        log.info(savedBook + " is saved to database successfully!");
        return savedBook;
    }

    public Book updateStock(String id, int stock) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            log.warn("The book specified by isbn number: " + id + " is not found!");
            return null;
        }
        Book existBook = book.get();
        existBook.setStock(stock);
        bookRepository.save(existBook);
        return existBook;
    }

    public Book getById(String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            log.warn("The book specified id number: " + id + " is not found!");
            return null;
        }
        return book.get();
    }
}
