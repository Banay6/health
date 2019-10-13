package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleDao {
    Set<Role> findByUid(Integer id);

    List<Role> findAll();

    void add(Role role);

    void setRoleAndPermission(Map<String, Integer> map);

    void setRoleAndMenu(Map<String, Integer> map);

    void deleteById(int id);

    void deleteRelationshipWithPermission(int id);

    void deleteRelationshipWithMenu(int id);

    Role findById(int id);

    List<Integer> findPermissionIdsByRoleId(int id);

    List<Integer> findMenuIdsByRoleId(int id);

    void edit(Role role);

    Page<Role> findPage(String queryString);
}
