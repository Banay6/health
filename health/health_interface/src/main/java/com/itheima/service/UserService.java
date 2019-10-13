package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.User;

import java.util.List;

public interface UserService {
    User findUserByUsername(String username);

    PageResult<User> findPage(QueryPageBean queryPageBean) throws Exception;

    void edit(User user, Integer[] roleIds);

    User findById(int id);

    List<Integer> findRoleIdsByUserId(int id);

    void add(User user, Integer[] roleIds);

    void deleteUserById(int id);
}
