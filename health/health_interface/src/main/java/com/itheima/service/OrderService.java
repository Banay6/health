package com.itheima.service;

import com.itheima.exception.HealthException;

import java.util.Map;

public interface OrderService {
    int addOrder(Map<String, String> orderInfo) throws HealthException;

    Map findById4Detail(int id);

}
