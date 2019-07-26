package com.fruitsalad.demo.sys.web;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fruitsalad.demo.sys.entity.User;
import com.fruitsalad.demo.sys.entity.UserAndUserInfo;
import com.fruitsalad.demo.sys.service.impl.UserServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wzh
 * @since 2019-07-17
 */
@Controller
@RequestMapping("/sys/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("list")
    public String getList(Model model) {
        User user = new User();
        Page<User> page = new Page<>(1, 3);
        EntityWrapper<User> wrapper = new EntityWrapper<>();
        page = user.selectPage(page, wrapper);
        System.out.println("数量：" + page.getTotal());
        System.out.println(page);
        model.addAttribute("page", page);
        return "sys/user";
    }

    @RequestMapping("fullInfo")
    public String getFullInfoList(Model model, @Param("username") String username) {
        User user = new User();
        user.setUsername(username);
        Page<UserAndUserInfo> page = new Page<>(1, 3);
        page.setRecords(userService.getUserFullInfoList(page, user));
        System.out.println(page);
        model.addAttribute("page", page);
        return "sys/userFullInfo";
    }
}
