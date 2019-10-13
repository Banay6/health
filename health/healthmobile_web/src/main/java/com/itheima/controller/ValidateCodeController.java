package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    // @PostMapping("/send4Order")
    // public Result send4Order(String telephone) {
    //     // 生成4位验证码
    //     Integer code = ValidateCodeUtils.generateValidateCode(4);
    //     try {
    //         SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());
    //     } catch (ClientException e) {
    //         e.printStackTrace();
    //         //发送失败
    //         return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    //     }
    //     System.out.println("发送的手机验证码为：" + code);
    //     // 将生成的验证码缓存到redis
    //     jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_ORDER, 5 * 60, code.toString());
    //     return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    // }

    @PostMapping("/send4Order")
    public Result send4Order(String telephone) {
        Jedis jedis = jedisPool.getResource();
        String key = telephone + RedisMessageConstant.SENDTYPE_ORDER;
        // 判断redis中是否有验证码
        if (null != jedis.get(key)) {
            // 已经发过了
            return new Result(false, MessageConstant.SENT_VALIDATECODE);
        }
        // 没发，生成验证码，发送
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());
            jedis.setex(key, 5 * 60, code.toString());
            System.out.println("发送的手机验证码为：" + code);
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }

    @PostMapping("/send4Login")
    public Result send4Login(String telephone) {
        Jedis jedis = jedisPool.getResource();
        String key = telephone + RedisMessageConstant.SENDTYPE_LOGIN;
        // 判断redis中是否有验证码
        if (null != jedis.get(key)) {
            // 已经发过了
            return new Result(false, MessageConstant.SENT_VALIDATECODE);
        }
        // 没发，生成验证码，发送
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code + "");
            jedis.setex(key, 5 * 60, code + "");
            System.out.println("发送的手机验证码为：" + code);
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);

    }

    // @PostMapping("/send4Login")
    // public Result send4Login(String telephone) {
    //     // 生成6位验证码
    //     Integer code = ValidateCodeUtils.generateValidateCode(6);
    //     try {
    //         SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());
    //     } catch (ClientException e) {
    //         e.printStackTrace();
    //         return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    //     }
    //     System.out.println("发送的手机验证码为：" + code);
    //     // 将生成的验证码缓存到redis
    //     jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN, 5 * 60, code.toString());
    //     return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    //
    // }
}
