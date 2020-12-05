package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    // Method to delete user. UserEntity as input will be deleted from DB
    // Only Admin can delete the user
    public boolean userDelete(final String userId, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0) {
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
