package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;

import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<String, LocalDateTime> DateTime = new HashMap<>();
            DateTime.put("begin", beginTime);
            DateTime.put("end", endTime);

            Double turnover = orderMapper.sumAmountByDateTimeAndStatus(DateTime, Orders.COMPLETED);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        // 每天新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        // 总用户数量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            Map<String, LocalDateTime> dateTimeMap = new HashMap<>();

            dateTimeMap.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalUserCount = userMapper.countUserByDateTime(dateTimeMap);

            dateTimeMap.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUserCount = userMapper.countUserByDateTime(dateTimeMap);

            totalUserList.add(totalUserCount);
            newUserList.add(newUserCount);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> vaildOrderCountList = new ArrayList<>();


        for (LocalDate date : dateList) {
            HashMap<String, LocalDateTime> dateTimeMap = new HashMap<>();
            dateTimeMap.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            dateTimeMap.put("end", LocalDateTime.of(date, LocalTime.MAX));
            // 查询每天的订单总数
            Integer OrderCountOfDay = orderMapper.countOrdersByDateTimeAndStatus(dateTimeMap, null);
            // 查询每天有效的订单数
            Integer vaildOrderCountOfDay = orderMapper.countOrdersByDateTimeAndStatus(dateTimeMap, Orders.COMPLETED);

            OrderCountOfDay = OrderCountOfDay == null ? 0 : OrderCountOfDay;
            orderCountList.add(OrderCountOfDay);
            vaildOrderCountOfDay = vaildOrderCountOfDay == null ? 0 : vaildOrderCountOfDay;
            vaildOrderCountList.add(vaildOrderCountOfDay);
        }

        // 合计订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer vaildOrderCount = vaildOrderCountList.stream().reduce(Integer::sum).get();

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (vaildOrderCount.doubleValue() / totalOrderCount) * 100;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(vaildOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(vaildOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTopList = orderMapper.getSalesTop(beginTime,endTime);

        List<String> names = salesTopList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTopList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }
}
