package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.exception.HealthException;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;

    // @PostMapping("/submit")
    // public Result submit(@RequestBody Map map) {
    //     String telephone = (String) map.get("telephone");
    //     // 从redis中获取缓存的验证码
    //     String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
    //     String validateCode = (String) map.get("validateCode");
    //     // 校验手机验证码
    //     if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
    //         return new Result(false, MessageConstant.VALIDATECODE_ERROR);
    //     }
    //     Result result = null;
    //
    //     try {
    //         map.put("orderType", Order.ORDERTYPE_WEIXIN);
    //         result = orderService.order(map);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return result;
    //     }
    //     if (result.isFlag()) {
    //         // 预约成功，短信通知
    //         String orderDate = (String) map.get("orderType");
    //         try {
    //             SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, orderDate);
    //         } catch (ClientException e) {
    //             e.printStackTrace();
    //         }
    //     }
    //     return result;
    // }

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, String> orderInfo) {
        // 手机号
        String telephone = orderInfo.get("telephone");
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        if (null == codeInRedis) {
            // 没有发或者已过期，提示从新发送
            return new Result(false, MessageConstant.SEND_VALIDATECODE);
        }
        // 校验验证码
        if (!codeInRedis.equals(orderInfo.get("validateCode"))) {
            // 不一致
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 微信预约的，设置进orderInfo
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        // 返回预约订单的id
        int orderId = orderService.addOrder(orderInfo);
        return new Result(true, MessageConstant.ORDER_SUCCESS, orderId);
    }

    @GetMapping("/findById")
    public Result findById4Detail(Integer id) {
        if (null == id) {
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
        Map map = orderService.findById4Detail(id);
        if (map != null) {
            // 处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate", DateUtils.parseDate2String(orderDate));
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, map);
        }
        return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
    }
}
