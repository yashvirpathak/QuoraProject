package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.Common;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private Common common;

    // Method to delete user. UserEntity as input will be deleted from DB
    // Only Admin can delete the user
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean userDelete(final String userId, final String token)
            throws AuthorizationFailedException, UserNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        if (userAuthTokenEntity.getUser().getRole().toLowerCase().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        // Check if user exists in DB
        UserEntity userEntity = userDao.getUserById(userId);
        if (userEntity == null || userEntity.getUuid().isEmpty()) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        // Deleting the user
        return userDao.deleteUser(userEntity);
    }
}
