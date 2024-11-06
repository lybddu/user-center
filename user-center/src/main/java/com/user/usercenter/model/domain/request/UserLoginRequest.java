package com.user.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author lyb
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = -6872849843473750023L;

    private String userAccount;

    private String userPassword;

}