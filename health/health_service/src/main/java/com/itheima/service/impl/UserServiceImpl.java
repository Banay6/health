package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public User findUserByUsername(String username) {
        User user = userDao.findUserByUsername(username);
        if (user != null) {
            // 根据用户id查询角色
            Set<Role> roles = roleDao.findByUid(user.getId());

            // 根据角色id查询角色
            if (roles != null && roles.size() > 0) {
                for (Role role : roles) {
                    Integer roleId = role.getId();
                    Set<Permission> permissions = permissionDao.findByRoleId(roleId);
                    role.setPermissions(permissions);
                }
                user.setRoles(roles);
            }
        }
        return user;
    }

    @Override
    public PageResult<User> findPage(QueryPageBean queryPageBean) throws Exception {
        // 如果QueryString不为空，拼接%%，模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }
        // 使用分页插件
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());

        // 分页查询
        Page<User> page = userDao.findPage(queryPageBean.getQueryString());
        List<User> result = page.getResult();

        return new PageResult<User>(page.getTotal(), result);
    }

    @Override
    @Transactional
    public void edit(User user, Integer[] roleIds) {
        // 编辑检查组，同时需要更新检查组的关联关系
        // 根据id删除中间表原有数据
        userDao.deleteRelationship(user.getId());
        // 向中间表插入数据
        setUserAndRole(user.getId(), roleIds);
        // 更新检查组基本信息
        userDao.edit(user);
    }

    @Override
    public User findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public List<Integer> findRoleIdsByUserId(int id) {
        return userDao.findRoleIdsByUserId(id);
    }

    @Override
    @Transactional
    public void add(User user, Integer[] roleIds) {
        // 添加检查组
        userDao.add(user);
        // 设置检查项和检查组的关联关系
        setUserAndRole(user.getId(), roleIds);
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        // 根据id删除中间表的关联
        userDao.deleteRelationship(id);
        // 根据id删除检查组
        userDao.deleteUserById(id);

    }

    private void setUserAndRole(Integer userId, Integer[] roleIds) {
        if (roleIds != null && roleIds.length > 0) {
            for (Integer roleId : roleIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("user_id", userId);
                map.put("role_id", roleId);
                userDao.setUserAndRole(map);
            }
        }
    }


}
