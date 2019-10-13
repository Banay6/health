package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Role;
import com.itheima.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Reference
    private RoleService roleService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADD')")
    public Result add(@RequestBody Role role, Integer[] permissionIds, Integer[] menuIds) {
        roleService.add(role, permissionIds, menuIds);
        return new Result(true, MessageConstant.ADD_ROLE_SUCCESS);
    }

    @PostMapping("/deleteById")
    @PreAuthorize("hasAnyAuthority('ROLE_DELETE')")
    public Result deleteById(int id) {
        roleService.deleteById(id);
        return new Result(true, MessageConstant.DELETE_ROLE_SUCCESS);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('ROLE_EDIT')")
    public Result edit(@RequestBody Role role, Integer[] permissionIds, Integer[] menuIds) {
        roleService.edit(role, permissionIds, menuIds);
        return new Result(true, MessageConstant.EDIT_ROLE_SUCCESS);
    }

    @PostMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('ROLE_QUERY')")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<Role> pageResult = roleService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, pageResult);
    }

    @GetMapping("/findAll")
    public Result findAll() {
        List<Role> list = roleService.findAll();
        return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, list);
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        Role role = roleService.findById(id);
        return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, role);
    }

    @GetMapping("/findPermissionIdsByRoleId")
    public Result findPermissionIdsByRoleId(int id) {
        List<Integer> ids = roleService.findPermissionIdsByRoleId(id);
        return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, ids);
    }

    @GetMapping("/findMenuIdsByRoleId")
    public Result findMenuIdsByRoleId(int id) {
        List<Integer> ids = roleService.findMenuIdsByRoleId(id);
        return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, ids);
    }
}
