package com.book.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("orders")
public class Order {
    @Id
    private String id;
    @NotBlank(message = "Book id number cannot be null or empty!")
    private String bookId;
    @NotBlank(message = "Book id number cannot be null or empty!")
    private String customerId;
    @Range(min = 1, message = "Item number cannot be smaller or equal to zero")
    private int itemNumber;
    @CreatedDate
    private LocalDateTime startDate;
    @Future
    @NotNull
    private LocalDateTime endDate;
    private BigDecimal amount;
}
