package com.book.service;

import com.book.domain.Order;
import com.book.domain.Statistic;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private static final Logger log = LoggerFactory.getLogger(StatisticService.class);
    private final OrderService orderService;

    @Autowired
    public StatisticService(OrderService orderService) {
        this.orderService = orderService;
    }

    public Map<String, Map<String, Statistic>> getStatistics() {
        List<Order> orders = this.orderService.getAllOrders();
        if (orders.isEmpty()) {
            log.info("There are no orders in the system. The statistic will not be produced!");
            return Collections.emptyMap();
        } else {
            Map<String, Map<String, Statistic>> statistics = new HashMap();
            log.info("Statistic is getting produced for each customer!");
            Iterator var3 = orders.iterator();

            while(var3.hasNext()) {
                Order order = (Order)var3.next();
                String month = order.getStartDate().getMonth().toString();
                String customerId = order.getCustomerId();
                if (statistics.get(customerId) == null) {
                    Map<String, Statistic> statisticByMont = new HashMap();
                    this.createFirstStatistic(order, statisticByMont, month, statistics, customerId);
                } else {
                    Map<String, Statistic> statisticByMont = (Map)statistics.get(customerId);
                    if (statisticByMont.get(month) == null) {
                        this.createFirstStatistic(order, statisticByMont, month, statistics, customerId);
                    } else {
                        Statistic current = (Statistic)statisticByMont.get(month);
                        this.updateCurrentStatistic(statistics, order, month, customerId, statisticByMont, current);
                    }
                }
            }

            return statistics;
        }
    }

    private void updateCurrentStatistic(Map<String, Map<String, Statistic>> statistics, Order order, String month, String customerId, Map<String, Statistic> statisticByMont, Statistic current) {
        current.setTotalOrderCount(current.getTotalOrderCount() + 1);
        current.setTotalBookCount(current.getTotalBookCount() + order.getItemNumber());
        current.setTotalPurchasedAmount(current.getTotalPurchasedAmount().add(order.getAmount()));
        statisticByMont.put(month, current);
        statistics.put(customerId, statisticByMont);
    }

    private void createFirstStatistic(Order order, Map<String, Statistic> statisticByMont, String month, Map<String, Map<String, Statistic>> statistics, String customerId) {
        Statistic statistic = Statistic.of(1, order.getItemNumber(), order.getAmount());
        statisticByMont.put(month, statistic);
        statistics.put(customerId, statisticByMont);
    }
}
