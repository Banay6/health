package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.utils.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/package")
public class PackageController {

    @Reference
    private PackageService packageService;
    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('PACKAGE_ADD')")
    public Result add(@RequestBody Package pack, Integer[] checkgroupIds) {
        packageService.add(pack, checkgroupIds);
        return new Result(true, MessageConstant.ADD_PACKAGE_SUCCESS);
    }

    @PostMapping("/deleteById")
    @PreAuthorize("hasAnyAuthority('PACKAGE_DELETE')")
    public Result deleteById(int id) {
        packageService.deleteById(id);
        return new Result(true, MessageConstant.DELETE_PACKAGE_SUCCESS);
    }

    @RequestMapping("/upload")
    @PreAuthorize("hasAnyAuthority('PACKAGE_EDIT')")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile) {
        try {
            // 获取原始文件名
            String originalFilename = imgFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            // 获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf - 1);
            // 使用uuid防止文件名重复
            String fileName = UUID.randomUUID().toString() + suffix;
            QiNiuUtil.uploadViaByte(imgFile.getBytes(), fileName);
            // 图片上传成功
            //将上传图片名称存入Redis，基于Redis的Set集合存储
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    @PostMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('PACKAGE_QUERY')")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<Package> pageResult = packageService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, pageResult);
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        Package pack = packageService.findById(id);
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, pack);
    }

    @GetMapping("/findCheckGroupByPackageId")
    public Result findCheckGroupByPackageId(int id) {
        List<Integer> ids = packageService.findCheckGroupByPackageId(id);
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, ids);
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody Package pack, Integer[] checkgroupIds) {
        packageService.edit(pack, checkgroupIds);
        return new Result(true, MessageConstant.EDIT_PACKAGE_SUCCESS);
    }


}
