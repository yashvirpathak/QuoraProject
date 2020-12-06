package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {


    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        UserEntity userByUsername = userDao.getUserByUsername(userEntity.getUserName());
        if (userByUsername != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        UserEntity userByEmail = userDao.getUserByEmail(userEntity.getEmail());
        if (userByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedPassword = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedPassword[0]);
        userEntity.setPassword(encryptedPassword[1]);
        return userDao.createUser(userEntity);

    }

    public UserEntity getUser(String userId, String userAuthorizationToken)
            throws UserNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(userAuthorizationToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if ((userAuthToken.getLogoutAt() != null) &&
                (userAuthToken.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) &&
                (userAuthToken.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        // Get user details
        UserEntity userEntity = userDao.getUserById(userId);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);

        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setUuid(userEntity.getUuid());
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            return userDao.createAuthToken(userAuthToken);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }


    public UserEntity signout(final String authorization) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);

        if (userAuthToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userAuthToken.setLogoutAt(ZonedDateTime.now());
        userDao.updateUserAuth(userAuthToken);
        UserEntity signedOutUser = userAuthToken.getUser();
        return signedOutUser;

    }

}
