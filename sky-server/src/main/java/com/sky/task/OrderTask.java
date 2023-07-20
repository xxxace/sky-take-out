package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?") // 每分钟触发一次
    public void processTimeoutOrder() {
        log.info("定时任务开始执行：{}", new Date());
        LocalDateTime orderTimeLT = LocalDateTime.now().minusMinutes(15);
        List<Orders> timeoutOrders = orderMapper.getTimeoutOrders(Orders.PENDING_PAYMENT, orderTimeLT);

        if (timeoutOrders != null && timeoutOrders.size() > 0) {
            for (Orders timeoutOrder : timeoutOrders) {
                timeoutOrder.setStatus(Orders.CANCELLED);
                timeoutOrder.setCancelReason("订单超时，自动取消");
                timeoutOrder.setCancelTime(LocalDateTime.now());
                orderMapper.update(timeoutOrder);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨一点触发一次
    public void processDeliveryOrder(){
        LocalDateTime yesterdayTime = LocalDateTime.now().minusHours(1);

        List<Orders> deliveryInProgressOrders = orderMapper.getTimeoutOrders(Orders.DELIVERY_IN_PROGRESS, yesterdayTime);

        if (deliveryInProgressOrders != null && deliveryInProgressOrders.size() > 0) {
            for (Orders deliveryInProgressOrder : deliveryInProgressOrders) {
                deliveryInProgressOrder.setStatus(Orders.COMPLETED);
                orderMapper.update(deliveryInProgressOrder);
            }
        }
    }
}
