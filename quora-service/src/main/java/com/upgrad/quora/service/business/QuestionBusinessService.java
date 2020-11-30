package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        return questionDao.createQuestion(questionEntity);

    }
}
