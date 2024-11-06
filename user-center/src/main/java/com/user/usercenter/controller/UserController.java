package com.user.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.usercenter.common.BaseResponse;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.common.ResultUtils;
import com.user.usercenter.exception.BusinessException;
import com.user.usercenter.model.domain.User;
import com.user.usercenter.model.domain.request.UserLoginRequest;
import com.user.usercenter.model.domain.request.UserRegisterRequest;
import com.user.usercenter.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.user.usercenter.constant.userConstant.ADMIN_ROLE;
import static com.user.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null){
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.NULL_ERROR,"注册信息为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userPassword,userAccount,checkPassword,planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"任何一项都不能为空");
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        return new BaseResponse<>(0,id,"ok");
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){

        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userPassword,userAccount)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"账号或密码为空");
        }
        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            return null;
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current" )
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser =(User) userObj;
        if(currentUser==null){
            return null;
        }
        long userId = currentUser.getId();

        User user = userService.getById(userId);
        User result = userService.getSafetyUser(user);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_AUTH,"不是管理员");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_AUTH,"不是管理员");
        }
        if(id <= 0){
            return null;
        }

        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    private boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user =(User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
