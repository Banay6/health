package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.PackageDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = PackageService.class)
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageDao packageDao;
    @Autowired
    private JedisPool jedisPool;


    @Override
    @Transactional
    public void add(Package pack, Integer[] checkgroupIds) {

        packageDao.add(pack);
        setPackageAndCheckGroup(pack.getId(), checkgroupIds);
        if (StringUtils.isEmpty(pack.getImg())) {
            throw new RuntimeException("请添加图片");
        }
        //将图片名称保存到Redis
        savePic2Redis(pack.getImg());
    }

    //将图片名称保存到Redis
    private void savePic2Redis(String pic) {
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, pic);
    }

    //将Redis中的图片名称删除
    private void deletePic2Redis(String pic) {
        if (pic == null) {
            return;
        }
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, pic);
    }

    @Override
    public PageResult<Package> findPage(QueryPageBean queryPageBean) {
        // 如果查询条件不为空，拼接%
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        Page<Package> page = packageDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public Package findById(int id) {
        return packageDao.findById(id);
    }

    @Override
    public List<Integer> findCheckGroupByPackageId(int id) {
        return packageDao.findCheckGroupByPackageId(id);
    }

    @Override
    @Transactional
    public void edit(Package pack, Integer[] checkgroupIds) {
        // 删除reids中原来保存的图片名
        deletePic2Redis(pack.getImg());
        // 将新的图片名称保存到redis
        savePic2Redis(pack.getImg());
        // 根据id删除中间表的关联
        packageDao.deleteRelationship(pack.getId());
        // 向中间表插入新数据
        setPackageAndCheckGroup(pack.getId(), checkgroupIds);
        // 更新套餐的基本信息
        packageDao.edit(pack);
    }

    @Override
    public void deleteById(int id) {
        // 根据id查询套餐
        Package pack = packageDao.findById(id);
        // 删除reids中原来保存的图片名
        deletePic2Redis(pack.getImg());
        // 根据id删除中间表关联
        packageDao.deleteRelationship(id);
        // 根据id删除套餐
        packageDao.deletePackageById(id);
    }

    @Override
    public List<Package> findAll() {
        return packageDao.findAll();
    }

    @Override
    public Package getPackageDetail(int id) {
        return packageDao.getPackageDetail(id);
    }

    @Override
    public List<Map<String, Object>> findPackageCount() {
        return packageDao.findPackageCount();
    }

    private void setPackageAndCheckGroup(Integer packageId, Integer[] checkgroupIds) {
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            for (Integer checkgroupId : checkgroupIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("package_id", packageId);
                map.put("checkgroup_id", checkgroupId);
                packageDao.setPackageAndCheckGroup(map);
            }
        }
    }
}
