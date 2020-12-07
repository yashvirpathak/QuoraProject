package com.upgrad.quora.service.common;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Common {
    @Autowired
    private UserDao userDao;

    public UserAuthTokenEntity validateUserToken(String token) throws AuthorizationFailedException {
        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(token);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        return userAuthTokenEntity;
    }
}
