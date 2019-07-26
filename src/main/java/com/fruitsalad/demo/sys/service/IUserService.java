package com.fruitsalad.demo.sys.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.fruitsalad.demo.sys.entity.User;
import com.fruitsalad.demo.sys.entity.UserAndUserInfo;
import com.fruitsalad.demo.sys.entity.UserInfo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wzh
 * @since 2019-07-17
 */
public interface IUserService extends IService<User> {

    public List<UserAndUserInfo> getUserFullInfoList(Page<UserAndUserInfo> page, User user);

}
