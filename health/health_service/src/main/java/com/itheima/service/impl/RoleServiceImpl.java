package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.RoleDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Role;
import com.itheima.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = RoleService.class)
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    @Transactional
    public void add(Role role, Integer[] permissionIds, Integer[] menuIds) {
        roleDao.add(role);
        setRoleAndPermission(role.getId(), permissionIds);
        setRoleAndMenu(role.getId(), menuIds);

    }

    @Override
    @Transactional
    public void deleteById(int id) {
        // 根据id删除中间表的关联
        roleDao.deleteRelationshipWithPermission(id);
        roleDao.deleteRelationshipWithMenu(id);

        roleDao.deleteById(id);

    }

    @Override
    public Role findById(int id) {
        return roleDao.findById(id);
    }

    @Override
    public List<Integer> findPermissionIdsByRoleId(int id) {
        return roleDao.findPermissionIdsByRoleId(id);
    }

    @Override
    public List<Integer> findMenuIdsByRoleId(int id) {
        return roleDao.findMenuIdsByRoleId(id);
    }

    @Override
    public void edit(Role role, Integer[] permissionIds, Integer[] menuIds) {

        roleDao.deleteRelationshipWithPermission(role.getId());
        roleDao.deleteRelationshipWithMenu(role.getId());
        // 向中间表插入数据
        setRoleAndPermission(role.getId(), permissionIds);
        setRoleAndMenu(role.getId(), menuIds);
        // 更新基本信息
        roleDao.edit(role);
    }

    @Override
    public PageResult<Role> findPage(QueryPageBean queryPageBean) {
        // 如果QueryString不为空，拼接%%，模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }
        // 使用分页插件
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());

        // 分页查询
        Page<Role> page = roleDao.findPage(queryPageBean.getQueryString());
        return new PageResult<Role>(page.getTotal(), page.getResult());
    }

    private void setRoleAndPermission(Integer roleId, Integer[] permissionIds) {
        if (permissionIds != null && permissionIds.length > 0) {
            for (Integer permissionId : permissionIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("role_id", roleId);
                map.put("permission_id", permissionId);
                roleDao.setRoleAndPermission(map);
            }
        }
    }

    private void setRoleAndMenu(Integer roleId, Integer[] menuIds) {
        if (menuIds != null && menuIds.length > 0) {
            for (Integer menuId : menuIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("role_id", roleId);
                map.put("menu_id", menuId);
                roleDao.setRoleAndMenu(map);
            }
        }
    }
}
