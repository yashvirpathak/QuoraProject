package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity){
        return entityManager.merge(questionEntity);
    }

    public boolean deleteQuestion(final QuestionEntity questionEntity) throws IllegalArgumentException{
        try{
            entityManager.getTransaction().begin();
            entityManager.remove(questionEntity);
            entityManager.getTransaction().commit();
        }catch (IllegalArgumentException iae){
            return false;
        }
        return true;
    }
}
