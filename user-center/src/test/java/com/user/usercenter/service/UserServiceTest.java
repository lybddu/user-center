package com.user.usercenter.service;
import java.util.Date;

import com.user.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 *
 * 用户服务测试
 *
 * @author lyb
 */

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;


    @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("dog");
        user.setUserAccount("dogyupi");
        user.setAvatarUrl("https://img0.baidu.com/it/u=3957537299,1483725619&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123 ");
        user.setEmail("456");
        user.setUserStatus(0);


        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }


    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "2";
        long result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yu";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yu#pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userPassword = "12345678";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "dogyupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yupi11";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertTrue(result > 0);
    }
}