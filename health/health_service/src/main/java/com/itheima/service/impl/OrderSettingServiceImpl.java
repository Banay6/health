package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    @Transactional
    public void add(List<OrderSetting> orderSettingList) {
        if (orderSettingList != null && orderSettingList.size() > 0) {
            for (OrderSetting orderSetting : orderSettingList) {
                // 查询此数据是否存在
                Long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (count > 0) {
                    // 存在,更新
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    // 不存在，添加
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        // 拼接开始时间
        String startDate = date + "-1";
        // 拼接结束时间
        String endDate = date + "-31";
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(startDate, endDate);
        List<Map> data = new ArrayList<>();
        for (OrderSetting orderSetting : list) {
            Map orderSettingMap = new HashMap();
            orderSettingMap.put("date", orderSetting.getOrderDate().getDate()); // 获取日期
            orderSettingMap.put("number", orderSetting.getNumber());
            orderSettingMap.put("reservations", orderSetting.getReservations());
            data.add(orderSettingMap);
        }
        return data;
    }

    @Override
    public void editNumberByDate(Date orderDate, int number) {
        OrderSetting orderSetting = new OrderSetting(orderDate, number);
        // 根据日期查询预约数
        Integer reservations = orderSettingDao.findReservationsByOrderDate(orderSetting.getOrderDate());
        if (reservations == null) {
            reservations = 0;
        }
        if (number < reservations) {
            throw new RuntimeException("修改的值不能小于已预约数");
        }
        // 查询此数据是否存在
        Long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count > 0) {
            // 存在,更新
            orderSettingDao.editNumberByOrderDate(orderSetting);
        } else {
            // 不存在，添加
            orderSettingDao.add(orderSetting);
        }

    }

    @Override
    public void clearOrderSetting(String firstDay4ThisMonth) {
        orderSettingDao.clearOrderSetting(firstDay4ThisMonth);
    }
}
