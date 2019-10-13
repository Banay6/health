package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    void add(Role role, Integer[] permissionIds, Integer[] menuIds);

    void deleteById(int id);

    Role findById(int id);

    List<Integer> findPermissionIdsByRoleId(int id);

    List<Integer> findMenuIdsByRoleId(int id);

    void edit(Role role, Integer[] permissionIds, Integer[] menuIds);

    PageResult<Role> findPage(QueryPageBean queryPageBean);
}
