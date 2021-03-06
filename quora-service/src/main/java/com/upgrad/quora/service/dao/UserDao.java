package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(final UserEntity userEntity) {
        entityManager.persist(userEntity);

        return userEntity;
    }

    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserById(final String userId) {
        try {
            return entityManager.createNamedQuery("userById", UserEntity.class).setParameter("uuid", userId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Method to delete user
    public boolean deleteUser(final UserEntity userEntity){
        try {
            // Delete user
            entityManager.remove(userEntity);
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    // Fetch user authentication token
    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            // Calling named query to get data
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Method to create user authentication token
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthToken) {
        entityManager.persist(userAuthToken);
        return userAuthToken;
    }

    //Method to delete user authentication token
    public void deleteAuthToken(final UserAuthTokenEntity userAuthToken) {
        entityManager.remove(userAuthToken);
    }

    //Method to update userAuth token
    @Transactional
    public void updateUserAuth(final UserAuthTokenEntity userAuthToken) {
        entityManager.merge(userAuthToken);
    }
}
