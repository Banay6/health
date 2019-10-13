package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Role;

import java.util.List;

public interface MenuDao {
    List<Role> findAll();

    void add(Menu menu);

    Page<Menu> findByCondition(String queryString);

    Menu findById(int id);

    int findLevelById(int id);

    List<Menu> findMenuByParentMenuId(int id);

    void deleteById(int id);

    void update(Menu menu);

    void deleteRelationship(int id);

    List<Menu> findByUsername(String username);
}
