package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Method to create new question. Persisting Question Entity in DB
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
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

    // Method to get single question details by Uuid
    public QuestionEntity getQuestionByUuid(final String questionId){
        try {
            // Calling named query to get data
            return entityManager.createNamedQuery("allQuestionById", QuestionEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }

    // Method to get all questions posted by any user
    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }

    // Method to edit content for given question
    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity){
        // merging the question entity
        return entityManager.merge(questionEntity);
    }

    // Method to delete question. QuestionEntity as input will be deleted from DB
    public boolean deleteQuestion(final QuestionEntity questionEntity) throws IllegalArgumentException{
        try{
            // Initiating delete question transaction
            entityManager.getTransaction().begin();
            entityManager.remove(questionEntity);
            entityManager.getTransaction().commit();
        }catch (IllegalArgumentException iae){
            return false;
        }
        return true;
    }

    // Method to get all questions owner by the user
    public List<QuestionEntity> getAllQuestionsByUser(final String userId){
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class)
                    .setParameter("uuid",userId )
                    .getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }
}