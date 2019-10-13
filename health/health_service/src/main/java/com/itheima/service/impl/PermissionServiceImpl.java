package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.PermissionDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

@Service(interfaceClass = PermissionService.class)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public List<Role> findAll() {
        return permissionDao.findAll();
    }

    @Override
    public void add(Permission permission) {
        permissionDao.add(permission);
    }

    @Override
    public PageResult<Permission> findPage(QueryPageBean queryPageBean) {
        // 条件处理，实现模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            // 如果queryString不为空，拼接%%
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }

        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        // 紧接着的查询语句会被分页
        Page<Permission> page = permissionDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(int id) {
        // 根据id删除中间表的关联
        permissionDao.deleteRelationship(id);
        permissionDao.deleteById(id);
    }

    @Override
    public Permission findById(int id) {
        return permissionDao.findById(id);
    }

    @Override
    public void update(Permission permission) {
        permissionDao.update(permission);
    }
}
