package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now())<0){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        return questionDao.createQuestion(questionEntity);

    }

    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now())<0){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        return questionDao.getAllQuestions();
    }

    public QuestionEntity editQuestionContent(final QuestionEntity editRequest, final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException{
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now())<0){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        if(editRequest.getUuid() == null || editRequest.getUuid().isEmpty()){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(!(userAuthTokenEntity.getUser().equals(editRequest.getUser()))){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }

        return questionDao.editQuestionContent(editRequest);
    }

    public boolean deleteQuestion(final String questionId, final String authorization)
    throws AuthorizationFailedException, InvalidQuestionException{

        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now())<0){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        QuestionEntity deleteRequest = questionDao.getQuestionByUuid(questionId);

        if(deleteRequest == null){
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if(!(userAuthTokenEntity.getUser().equals(deleteRequest.getUser()) || deleteRequest.getUser().getRole().toLowerCase().equals("admin"))){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }

        return questionDao.deleteQuestion(deleteRequest);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now())<0){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // Call userDao to check whether user exists or not

        return questionDao.getAllQuestionsByUser(userId);
    }
}
