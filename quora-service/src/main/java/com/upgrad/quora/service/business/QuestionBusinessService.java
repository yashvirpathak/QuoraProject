package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.Common;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private Common common;

    // Method to create new question. Persisting Question Entity in DB
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String token) throws AuthorizationFailedException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // Creating new question
        questionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    // Method to get all questions posted by any user
    public List<QuestionEntity> getAllQuestions(final String token) throws AuthorizationFailedException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // retuning all questions
        return questionDao.getAllQuestions();
    }

    // Method to edit content for given question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final QuestionEntity editRequest, final String token)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // Check if question is valid
        if (editRequest == null || editRequest.getUuid().isEmpty()) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (!(userAuthTokenEntity.getUser().equals(editRequest.getUser()))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        // Updating the question content in DB
        return questionDao.editQuestionContent(editRequest);
    }

    // Method to delete question. QuestionEntity as input will be deleted from DB
    // Only Question owner can delete the question
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteQuestion(final String questionId, final String token)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // Checking if question exists in DB
        QuestionEntity deleteRequest = questionDao.getQuestionByUuid(questionId);

        if (deleteRequest == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (!(userAuthTokenEntity.getUser().equals(deleteRequest.getUser()) || deleteRequest.getUser().getRole().toLowerCase().equals("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        // Deleting the question
        return questionDao.deleteQuestion(deleteRequest);
    }

    // Method to get all questions owner by the user
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String token)
            throws AuthorizationFailedException, UserNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = common.validateUserToken(token);

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        // Call userDao to check whether user exists or not
        UserEntity userEntity = userDao.getUserById(userId);
        if (userEntity == null || userEntity.getUuid().isEmpty()) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        // return all questions owned by user
        return questionDao.getAllQuestionsByUser(userId);
    }

    // Get Question details by Uuid
    public QuestionEntity getQuestionByUuid(final String questionId) throws InvalidQuestionException {

        // Check if Question exists in DB or not
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null || questionEntity.getUuid().isEmpty()) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        // return question entity
        return questionEntity;
    }
}
