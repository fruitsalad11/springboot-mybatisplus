package com.fruitsalad.demo.sys.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fruitsalad.demo.sys.entity.User;
import com.fruitsalad.demo.sys.entity.UserAndUserInfo;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wzh
 * @since 2019-07-17
 */
public interface UserMapper extends BaseMapper<User> {

    public List<UserAndUserInfo> getUserList(Page<UserAndUserInfo> page, User user);
}
