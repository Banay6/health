package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.util.List;

public interface PermissionService {
    List<Role> findAll();

    void add(Permission permission);

    PageResult<Permission> findPage(QueryPageBean queryPageBean);

    void deleteById(int id);

    Permission findById(int id);

    void update(Permission permission);
}
