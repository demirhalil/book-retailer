package com.book.util;

import com.book.domain.Book;
import com.book.domain.Customer;
import com.book.domain.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {
    public static final String MOCK_USER_NAME = "mock-user";
    public static final String MOCK_PASSWORD = "mock-password";
    private static final Random RANDOM = new Random();
    private static final ThreadLocalRandom THREAD_LOCAL_RANDOM = ThreadLocalRandom.current();
    private static final String EMAIL_SUFFIX = "@email.com";

    public static Customer createCustomer(String name, String lastName) {
        int uniqueIdentifier = RANDOM.nextInt(1_000);
        String email = name
            + "."
            + lastName
            + uniqueIdentifier
            + EMAIL_SUFFIX;
        return new Customer(null, name, lastName, email);
    }

    public static Book createBook(String name, String author) {
        String isbn = String.valueOf(THREAD_LOCAL_RANDOM.nextLong(10_000_000_000L, 100_000_000_000L));
        int stock = RANDOM.nextInt(1_000);
        BigDecimal price = createPrice();
        return new Book(null, isbn, name, author, stock, price);
    }

    public static Order createOrder(Book book, int itemNumber) {
        final LocalDateTime now = LocalDateTime.now();
        final BigDecimal amount = book.getPrice().multiply(BigDecimal.valueOf(itemNumber));
        return new Order(null, book.getIsbn(), "test-customer-" + itemNumber, itemNumber, now, now.plusDays(10), amount);
    }

    private static BigDecimal createPrice() {
        int range = 1_000;
        BigDecimal max = new BigDecimal(range);
        BigDecimal randFromDouble = BigDecimal.valueOf(Math.random());
        BigDecimal actualRandomDec = randFromDouble.multiply(max);
        actualRandomDec = actualRandomDec
            .setScale(2, RoundingMode.DOWN);
        return actualRandomDec;
    }
}
