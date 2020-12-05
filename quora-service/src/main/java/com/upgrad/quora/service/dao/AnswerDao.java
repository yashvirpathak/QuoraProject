package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Method to create an Answer
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
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
            return entityManager.createNamedQuery("answerById", AnswerEntity.class)
                    .setParameter("uuid",answerId)
                    .getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }
}
