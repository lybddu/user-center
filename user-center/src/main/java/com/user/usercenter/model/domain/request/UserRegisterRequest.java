package com.user.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author lyb
 */
@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = -6872849843473750023L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
