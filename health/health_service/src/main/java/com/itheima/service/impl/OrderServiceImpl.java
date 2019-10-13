package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.exception.HealthException;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;

    // @Override
    // @Transactional
    // public Result order(Map map) throws ParseException {
    //     // 判断当前日期是否设置了预约
    //     String orderDate = (String) map.get("orderDate");
    //     Date date = DateUtils.parseString2Date(orderDate);
    //     OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
    //     if (orderSetting == null) {
    //         return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
    //     }
    //     // 判断当前日期是否预约满了
    //     int number = orderSetting.getNumber();// 可预约人数
    //     int reservations = orderSetting.getReservations(); //已预约人数
    //     if (reservations >= number) {
    //         return new Result(false, MessageConstant.ORDER_FULL);
    //     }
    //     // 判断当前用户是否是会员
    //     String telephone = (String) map.get("telephone");
    //     Member member = memberDao.findByTelephone(telephone);
    //
    //     if (member != null) {
    //         // 是会员，避免重复预约
    //         Integer memberId = member.getId();
    //         int packageId = Integer.parseInt((String) map.get("packageId"));
    //         Order order = new Order(memberId, date, null, null, packageId);
    //         List<Order> list = orderDao.findByCondition(order);
    //         if (list != null && list.size() > 0) {
    //             return new Result(false, MessageConstant.HAS_ORDERED);
    //         }
    //     } else {
    //         // 不是会员，加入会员表
    //         member = new Member();
    //         member.setName((String) map.get("name"));
    //         member.setPhoneNumber(telephone);
    //         member.setIdCard((String) map.get("idCard"));
    //         member.setSex((String) map.get("sex"));
    //         member.setRegTime(new Date());
    //         memberDao.add(member);
    //     }
    //     // 进行预约
    //     // 预约人数+1
    //     orderSetting.setReservations(orderSetting.getReservations() + 1);
    //     orderSettingDao.editReservationsByOrderDate(orderSetting);
    //
    //     // 保存预约信息到预约表
    //     Order order = new Order(member.getId(), date, (String) map.get("orderType"), Order.ORDERSTATUS_NO, Integer.parseInt((String) map.get("packageId")));
    //     orderDao.add(order);
    //     return new Result(true, MessageConstant.ORDER_SUCCESS, order);
    // }


    @Override
    @Transactional
    public int addOrder(Map<String, String> orderInfo) throws HealthException {
        // 这个日期是否可以预约
        String orderDate = orderInfo.get("orderDate");
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(orderDate);
        // 如果为空，这日不能预约
        if (orderSetting == null) {
            throw new HealthException(MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        // 不为空,判断已预约人数是否大于等于可预约人数
        if (orderSetting.getReservations() >= orderSetting.getNumber()) {
            throw new HealthException(MessageConstant.ORDER_FULL);
        }
        // 判断是否为会员,通过手机号查询
        String telephone = orderInfo.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if (null == member) {
            // 不存在，添加
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setIdCard(orderInfo.get("idCard"));
            member.setName(orderInfo.get("name"));
            member.setSex(orderInfo.get("sex"));
            memberDao.add(member);
        }
        // 存在，取出会员的id
        Integer memberId = member.getId();
        // 判断是否重复预约
        Order order = new Order();
        order.setMemberId(memberId);
        try {
            order.setOrderDate(DateUtils.parseString2Date(orderDate));
        } catch (Exception e) {
            throw new HealthException(MessageConstant.ORDER_FAIL);
        }
        order.setPackageId(Integer.valueOf(orderInfo.get("packageId")));
        // 根据会员id，预约日期，套餐id查询是否有预约记录，一天可预约多个套餐
        List<Order> orders = orderDao.findByCondition(order);
        if (null != orders && orders.size() > 0) {
            // 已经预约了
            throw new HealthException(MessageConstant.HAS_ORDERED);
        }
        // 没有预约，插入
        // 完善order信息
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setOrderType(orderInfo.get("orderType"));
        orderDao.add(order);

        // 增加已预约的人数
        orderSettingDao.editReservationsByOrderDate(1, orderDate);

        return order.getId();
    }

    @Override
    public Map findById4Detail(int id) {
        return orderDao.findById4Detail(id);
    }
}
