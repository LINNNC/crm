package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        User user = userDao.login(map);

        if (user == null){
            throw new LoginException("用户名或密码错误");
        }

        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw new LoginException("账号已失效");
        }

        String locaState = user.getLockState();
        if("0".equals(locaState)){
            throw new LoginException("账号已被封禁");
        }

        String allowIps = user.getAllowIps();
        System.out.println("userIp==================>"+allowIps);
        System.out.println("------------------------>"+ip);
        if(!allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }

        return user;
    }
}
