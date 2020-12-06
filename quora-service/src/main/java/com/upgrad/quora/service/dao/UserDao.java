package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

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
            // Initiating delete question transaction
            entityManager.getTransaction().begin();
            entityManager.remove(userEntity);
            entityManager.getTransaction().commit();
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
}
