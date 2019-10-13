package com.itheima.dao;

import com.itheima.pojo.Member;

import java.util.List;
import java.util.Map;

public interface MemberDao {
    Member findByTelephone(String telephone);

    void add(Member member);

    Integer findMemberCountBeforeDate(String month);

    Integer findMemberCountByDate(String today);

    Integer findMemberTotalCount();

    Integer findMemberCountAfterDate(String thisWeekMonday);

    List<Map<String, Object>> getGender();

    List<Map<String, Object>> getAge();
}
