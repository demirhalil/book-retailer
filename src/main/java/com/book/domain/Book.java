package com.book.domain;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "books")
public class Book{
    @Id
    private String id;
    @NotBlank(message = "Book isbn number cannot be null or empty!")
    @Indexed
    private String isbn;
    @NotBlank(message = "Book name cannot be null or empty!")
    private String name;
    @NotBlank(message = "Book author name of book cannot be null or empty!")
    private String author;
    @Range(min = 0, max = Integer.MAX_VALUE, message = "Stock cannot be smaller than zero!")
    private int stock;
    @Range(min = 0, message = "The price must be greater than zero!")
    private BigDecimal price;
}
