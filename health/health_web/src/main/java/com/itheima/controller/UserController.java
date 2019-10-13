package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.UserService;
import com.itheima.utils.DateUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('USER_ADD')")
    public Result add(@RequestBody com.itheima.pojo.User user, Integer[] roleIds) {
        userService.add(user, roleIds);
        return new Result(true, MessageConstant.ADD_USER_SUCCESS);
    }

    @PostMapping("/deleteById")
    @PreAuthorize("hasAnyAuthority('USER_DELETE')")
    public Result deleteById(int id) {
        userService.deleteUserById(id);
        return new Result(true, MessageConstant.DELETE_USER_SUCCESS);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('USER_EDIT')")
    public Result edit(@RequestBody com.itheima.pojo.User user, Integer[] RoleIds) {
        userService.edit(user, RoleIds);
        return new Result(true, MessageConstant.EDIT_USER_SUCCESS);
    }

    @PostMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('USER_QUERY')")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) throws Exception {
        PageResult<com.itheima.pojo.User> pageResult = userService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_USER_SUCCESS, pageResult);
    }

    //获取当前登录用户的用户名
    @GetMapping("/getUserName")
    public Result getUserName() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, user.getUsername());
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        com.itheima.pojo.User user = userService.findById(id);
        return new Result(true, MessageConstant.QUERY_USER_SUCCESS, user);
    }

    @GetMapping("/findRoleIdsByUserId")
    public Result findRoleIdsByUserId(int id) {
        List<Integer> ids = userService.findRoleIdsByUserId(id);
        return new Result(true, MessageConstant.QUERY_USER_SUCCESS, ids);
    }

}
