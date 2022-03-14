package com.book.repository;

import com.book.domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByCustomerId(String customerId);
    List<Order> findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);
}
