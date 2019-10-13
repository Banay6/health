package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.PermissionService;
import com.itheima.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('PERMISSION_ADD')")
    public Result add(@RequestBody Permission permission) {
        permissionService.add(permission);
        return new Result(true, MessageConstant.ADD_PERMISSION_SUCCESS);
    }

    @PostMapping("/deleteById")
    @PreAuthorize("hasAnyAuthority('PERMISSION_DELETE')")
    public Result deleteById(int id) {
        permissionService.deleteById(id);
        return new Result(true, MessageConstant.DELETE_PERMISSION_SUCCESS);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('PERMISSION_EDIT')")
    public Result update(@RequestBody Permission permission) {
        permissionService.update(permission);
        return new Result(true, MessageConstant.EDIT_PERMISSION_SUCCESS);
    }

    @PostMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('PERMISSION_QUERY')")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<Permission> pageResult = permissionService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_PERMISSION_SUCCESS, pageResult);
    }

    @GetMapping("/findAll")
    public Result findAll() {
        List<Role> list = permissionService.findAll();
        return new Result(true, MessageConstant.QUERY_PERMISSION_SUCCESS, list);
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        Permission permission = permissionService.findById(id);
        return new Result(true, MessageConstant.QUERY_PERMISSION_SUCCESS, permission);
    }

}
