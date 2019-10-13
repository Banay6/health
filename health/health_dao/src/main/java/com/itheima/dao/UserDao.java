package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    Page<User> findPage(String queryString);

    User findUserByUsername(String username);

    void deleteRelationship(Integer id);

    void setUserAndRole(Map<String, Integer> map);

    void edit(User user);

    User findById(int id);

    List<Integer> findRoleIdsByUserId(int id);

    void add(User user);

    void deleteUserById(int id);
}
