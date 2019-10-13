package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.utils.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@RestController
@RequestMapping("/package")
public class PackageController {

    @Reference
    private PackageService packageService;
    @Autowired
    private JedisPool jedisPool;

    @GetMapping("/getPackage")
    public Result getPackage() {
        List<Package> list = null;
        try {
            String key = RedisConstant.PACKAGE_PAGES;
            Jedis jedis = jedisPool.getResource();
            String jsonStr = jedis.get(RedisConstant.PACKAGE_PAGES);
            ObjectMapper mapper = new ObjectMapper();
            if (null == jsonStr) {
                list = packageService.findAll();
                jsonStr = mapper.writeValueAsString(list);
                jedis.set(RedisConstant.PACKAGE_PAGES, jsonStr);
                return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, jsonStr);
            }
            return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PACKAGE_FAIL);
        }
    }

    @GetMapping("/getPackageDetail")
    public Result getPackageDetail(int id) {
        Package pack = packageService.getPackageDetail(id);
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, pack);
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        Package pack = packageService.findById(id);
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, pack);
    }

}
