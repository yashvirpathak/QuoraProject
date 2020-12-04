package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity){
        return entityManager.merge(answerEntity);
    }

    public boolean deleteAnswer(final AnswerEntity answerEntity) throws IllegalArgumentException{
        try{
            entityManager.getTransaction().begin();
            entityManager.remove(answerEntity);
            entityManager.getTransaction().commit();
        }catch (IllegalArgumentException iae){
            return false;
        }
        return true;
    }

    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId){
        try {
            return entityManager.createNamedQuery("allAnswersToQuestion", AnswerEntity.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }

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
