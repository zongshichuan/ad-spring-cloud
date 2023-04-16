package com.chuan.ad.service;


import com.chuan.ad.exception.AdException;
import com.chuan.ad.vo.CreateUserRequest;
import com.chuan.ad.vo.CreateUserResponse;

public interface IUserService {

    /**
     * <h2>创建用户</h2>
     * */
    CreateUserResponse createUser(CreateUserRequest request)
            throws AdException;
}
