package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Reference
    private MemberService memberService;
    @Autowired
    private JedisPool jedisPool;

    /**
     * 使用手机号和验证码登录
     *
     * @param map
     * @return
     */
    @PostMapping("/check")
    public Result check(@RequestBody Map map, HttpServletResponse res) {
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        // 从redis中获取验证码
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            // 验证码过期或错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        } else {
            // 验证码正确
            // 判断是否是会员
            Member member = memberService.findByTelephone(telephone);
            if (member == null) {
                // 不是会员，自动注册
                member = new Member();
                member.setPhoneNumber(telephone);
                member.setRegTime(new Date());
                memberService.add(member);
            }
            // 用户跟踪，写手机号码到Cookie，当用户再次访问我们的网站时就会带上这个cookie，这样我们就知道是哪个用户了，方便日后做统计分析
            Cookie cookie = CookieUtil.createCookie("login_member_telephone", telephone, 60 * 60 * 24 * 30, "/");
            res.addCookie(cookie);
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }
    }
}
