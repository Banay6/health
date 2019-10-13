package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Package;

import java.util.List;
import java.util.Map;

public interface PackageDao {
    List<Package> findAll();

    void add(Package pack);

    void setPackageAndCheckGroup(Map<String, Integer> map);

    Page<Package> findByCondition(String queryString);

    Package findById(int id);

    List<Integer> findCheckGroupByPackageId(int id);

    void deleteRelationship(Integer id);

    void edit(Package pack);

    void deletePackageById(int id);


    Package getPackageDetail(int id);

    List<Map<String, Object>> findPackageCount();
}
