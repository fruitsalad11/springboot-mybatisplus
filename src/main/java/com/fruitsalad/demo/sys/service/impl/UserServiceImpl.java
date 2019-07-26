package com.fruitsalad.demo.sys.service.impl;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fruitsalad.demo.sys.entity.User;
import com.fruitsalad.demo.sys.entity.UserAndUserInfo;
import com.fruitsalad.demo.sys.entity.UserInfo;
import com.fruitsalad.demo.sys.mapper.UserMapper;
import com.fruitsalad.demo.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wzh
 * @since 2019-07-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;

    public List<UserAndUserInfo> getUserFullInfoList(Page<UserAndUserInfo> page, User user) {
        return userMapper.getUserList(page,user);
    }

}
