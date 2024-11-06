package com.user.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.usercenter.common.ErrorCode;
import com.user.usercenter.exception.BusinessException;
import com.user.usercenter.model.domain.User;
import com.user.usercenter.service.UserService;
import com.user.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.user.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author liyb
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-09-26 11:51:48
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "lyb";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"任何一项都不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度不能小于4");
        }
        if(userPassword.length() < 8 || checkPassword.length() <8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和二次密码不能小于8");
        }
        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不能大于5位数");
        }
        //用户名不含特殊字符
        String vailidPattern = "/^[a-zA-Z0-9_-]{4,16}$/";
        Matcher matcher = Pattern.compile(vailidPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能含特殊字符");
        }
        //密码和校验码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验码不同");
        }
        //用户不能重复
        QueryWrapper<User> queryMapper = new QueryWrapper<>();
        queryMapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryMapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能重复");
        }
        //星球编号不能重复
        queryMapper = new QueryWrapper<>();
        queryMapper.eq("planetCode",userAccount);
        count = userMapper.selectCount(queryMapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不能重复");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"账号或密码不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于4");
        }
        //用户名不含特殊字符
        String vailidPattern = "/^[a-zA-Z0-9_-]{4,16}$/";
        Matcher matcher = Pattern.compile(vailidPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名不能包含特殊字符");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        //查寻用户是否存在
        QueryWrapper<User> queryMapper = new QueryWrapper<>();
        queryMapper.eq("userAccount",userAccount);
        queryMapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryMapper);
        //用户不存在
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号与密码不匹配或者未注册");
        }

        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);

        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);


        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originuser
     * @return
     */
    @Override
    public User getSafetyUser(User originuser){
        if(originuser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originuser.getId());
        safetyUser.setUsername(originuser.getUsername());
        safetyUser.setUserAccount(originuser.getUserAccount());
        safetyUser.setAvatarUrl(originuser.getAvatarUrl());
        safetyUser.setGender(originuser.getGender());
        safetyUser.setPhone(originuser.getPhone());
        safetyUser.setEmail(originuser.getEmail());
        safetyUser.setPlanetCode(originuser.getPlanetCode());
        safetyUser.setUserStatus(originuser.getUserStatus());
        safetyUser.setCreateTime(originuser.getCreateTime());
        safetyUser.setUserRole(originuser.getUserRole());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




