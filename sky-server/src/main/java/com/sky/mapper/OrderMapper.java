package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    Long insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    List<Orders> getTimeoutOrders(Integer status, LocalDateTime orderTimeLT);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    Double sumAmountByDateTimeAndStatus(Map<String, LocalDateTime> dateTime, Integer status);

    Integer countOrdersByDateTimeAndStatus(Map<String, LocalDateTime> dateTime, Integer status);

    List<GoodsSalesDTO> getSalesTop(LocalDateTime begin, LocalDateTime end);
}
