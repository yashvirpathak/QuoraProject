package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    // Method to create an Answer
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String authorization)
            throws AuthorizationFailedException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        // Create answer
        return answerDao.createAnswer(answerEntity);
    }

    // Method to delete an answer
    public boolean deleteAnswer(final String answerId, final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        // Check if answer already exists in DB
        AnswerEntity deleteRequest = answerDao.getAnswerByUuid(answerId);

        if (deleteRequest == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Throw exception if user is not owner of answer or not an admin
        if (!(userAuthTokenEntity.getUser().equals(deleteRequest.getUser()) || deleteRequest.getUser().getRole().toLowerCase().equals("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        // Delete the answer
        return answerDao.deleteAnswer(deleteRequest);
    }

    // Method to get all answers for given question
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId, final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Authorize user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        // Check if Question exists in DB
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null  || questionEntity.getUuid().isEmpty()) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        // Get all questions for the given question
        return answerDao.getAllAnswersToQuestion(questionId);
    }
}
