package com.book.controller;

import com.book.domain.Statistic;
import com.book.service.StatisticService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
@RequestMapping({"/statistics"})
public class StatisticController {
    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Map<String, Statistic>>> getStatistics() {
        return new ResponseEntity(this.statisticService.getStatistics(), HttpStatus.OK);
    }
}