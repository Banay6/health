package com.itheima.dao;

import com.itheima.pojo.OrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OrderSettingDao {
    Integer findReservationsByOrderDate(Date orderDate);

    Long findCountByOrderDate(Date orderDate);

    void editNumberByOrderDate(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonth(@Param("startDate") String startDate, @Param("endDate") String endDate);

    OrderSetting findByOrderDate(String date);

    void editReservationsByOrderDate(@Param("num") int num,@Param("orderDate") String orderDate);

    void clearOrderSetting(String firstDay4ThisMonth);
}
