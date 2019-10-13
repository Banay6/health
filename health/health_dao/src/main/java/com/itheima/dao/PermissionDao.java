package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Set;

public interface PermissionDao {
    Set<Permission> findByRoleId(Integer roleId);

    List<Role> findAll();

    void add(Permission permission);

    Page<Permission> findByCondition(String queryString);

    void deleteRelationship(int id);

    void deleteById(int id);

    Permission findById(int id);

    void update(Permission permission);
}
