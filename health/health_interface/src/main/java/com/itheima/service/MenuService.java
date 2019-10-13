package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.HealthException;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Map;

public interface MenuService {
    List<Role> findAll();

    void add(Menu menu);

    PageResult<Menu> findPage(QueryPageBean queryPageBean);

    void deleteById(int id) throws HealthException;

    Menu findById(int id);

    void update(Menu menu);

    List<Menu> findByUsername(String username);
}
