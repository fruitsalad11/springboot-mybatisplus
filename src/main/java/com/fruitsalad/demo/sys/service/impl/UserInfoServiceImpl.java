package com.fruitsalad.demo.sys.service.impl;

import com.fruitsalad.demo.sys.entity.UserInfo;
import com.fruitsalad.demo.sys.mapper.UserInfoMapper;
import com.fruitsalad.demo.sys.service.IUserInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wzh
 * @since 2019-07-18
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
