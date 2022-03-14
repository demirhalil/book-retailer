package com.book.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class BookServiceMongoConfiguration extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "book-retailer";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
