package com.book.domain;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;
    @NotBlank(message = "Customer name cannot be null or empty!")
    private String name;
    @NotBlank(message = "Customer last name cannot be null or empty!")
    private String lastName;
    @NotBlank(message = "Customer email address cannot be null or empty!")
    @Indexed(unique = true,name = "EMAIL_ADDRESS_INDEX")
    @Email
    private String email;
}
