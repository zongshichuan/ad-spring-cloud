package com.chuan.ad.service.impl;

import com.chuan.ad.constant.Constants;
import com.chuan.ad.dao.AdUserRepository;
import com.chuan.ad.entity.AdUser;
import com.chuan.ad.exception.AdException;
import com.chuan.ad.service.IUserService;
import com.chuan.ad.utils.CommonUtils;
import com.chuan.ad.vo.CreateUserRequest;
import com.chuan.ad.vo.CreateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final AdUserRepository userRepository;

    @Autowired
    public UserServiceImpl(AdUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request)
            throws AdException {

        if (!request.validate()) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdUser oldUser = userRepository.
                findByUsername(request.getUsername());
        if (oldUser != null) {
            throw new AdException(Constants.ErrorMsg.SAME_NAME_ERROR);
        }

        AdUser newUser = userRepository.save(new AdUser(
                request.getUsername(),
                CommonUtils.md5(request.getUsername())
        ));

        return CreateUserResponse.builder()
                .userId(newUser.getId())
                .username(newUser.getUsername())
                .token(newUser.getToken())
                .createTime(newUser.getCreateTime())
                .updateTime(newUser.getUpdateTime())
                .build();
    }
}
