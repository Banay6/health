package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Package;

import java.util.List;
import java.util.Map;

public interface PackageService {
    void add(Package pack, Integer[] checkgroupIds);

    PageResult<Package> findPage(QueryPageBean queryPageBean);

    Package findById(int id);

    List<Integer> findCheckGroupByPackageId(int id);

    void edit(Package pack, Integer[] checkgroupIds);

    void deleteById(int id);

    List<Package> findAll();

    Package getPackageDetail(int id);

    List<Map<String, Object>> findPackageCount();
}
