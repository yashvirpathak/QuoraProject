package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException{
        /*UserAuthTokenEntity userAuthTokenEntity = UserDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }*/

        //QuestionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);

    }

    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        /*UserAuthTokenEntity userAuthTokenEntity = UserDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }*/
        return questionDao.getAllQuestions();
    }

    public QuestionEntity editQuestionContent(final QuestionEntity editRequest, final String authorization)
    throws AuthorizationFailedException, InvalidQuestionException{
        /*UserAuthTokenEntity userAuthTokenEntity = UserDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }*/

        return questionDao.editQuestionContent(editRequest);
    }

    public boolean deleteQuestion(final String questionId, final String authorization)
    throws AuthorizationFailedException, InvalidQuestionException{

        /*UserAuthTokenEntity userAuthTokenEntity = UserDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }*/
        QuestionEntity deleteRequest = questionDao.getQuestionByUuid(questionId);

        if(deleteRequest == null){
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        return questionDao.deleteQuestion(deleteRequest);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        /*UserAuthTokenEntity userAuthTokenEntity = UserDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }*/
        return questionDao.getAllQuestionsByUser(userId);
    }
}
