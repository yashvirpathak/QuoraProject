package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Method to create an Answer
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    // Method to update the answer
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity){
        return entityManager.merge(answerEntity);
    }

    // Method to delete an answer
    public boolean deleteAnswer(final AnswerEntity answerEntity) throws IllegalArgumentException{
        try{
            // Initiating transaction to remove answer
            entityManager.getTransaction().begin();
            entityManager.remove(answerEntity);
            entityManager.getTransaction().commit();
        }catch (IllegalArgumentException iae){
            return false;
        }
        return true;
    }

    // Method to get Answer Entity based on Uuid
    public AnswerEntity getAnswerByUuid(final String answerId){
        try {
            // Calling named query to get data
            return entityManager.createNamedQuery("answerById", AnswerEntity.class)
                    .setParameter("uuid",answerId)
                    .getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }

    // Method to get all answers for given question
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId){
        try {
            // Calling named query to get data
            return entityManager.createNamedQuery("allAnswersToQuestion", AnswerEntity.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }
}
