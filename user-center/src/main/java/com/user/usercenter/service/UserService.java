package com.user.usercenter.service;

import com.user.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;


/**
* @author liyb
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-09-26 11:51:48
*/
public interface UserService extends IService<User> {


    /**
     *  用户注册
     * @param userAccount 账户
     * @param userPassword
     * @param checkPassword
     * @param planetCode 星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request );

    /**
     * 用户脱敏
     * @param originuser
     * @return
     */
    User getSafetyUser(User originuser);
    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest   request);
}
