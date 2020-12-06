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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    // Method to create an Answer
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String authorization)
            throws AuthorizationFailedException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        // Create answer
        answerEntity.setUser(userAuthTokenEntity.getUser());
        return answerDao.createAnswer(answerEntity);
    }

    // Method to edit Answer content
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity editAnswerEntity, final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        // Check if Answer is valid or not
        AnswerEntity answerEntity = answerDao.getAnswerByUuid(editAnswerEntity.getUuid());
        if (answerEntity == null || answerEntity.getUuid().isEmpty()) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Checking if user is owner of the answer
        if (!(userAuthTokenEntity.getUser().equals(answerEntity.getUser()))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        // Update/edit the answer
        editAnswerEntity.setUuid(answerEntity.getUuid());
        editAnswerEntity.setUser(answerEntity.getUser());
        editAnswerEntity.setId(answerEntity.getId());
        editAnswerEntity.setCreateDate(answerEntity.getCreateDate());
        editAnswerEntity.setQuestion(answerEntity.getQuestion());
        return answerDao.editAnswerContent(editAnswerEntity);
    }

    // Method to delete an answer
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteAnswer(final String answerId, final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        // Authorizing user
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        // Check if answer already exists in DB
        AnswerEntity deleteRequest = answerDao.getAnswerByUuid(answerId);

        if (deleteRequest == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Throw exception if user is not owner of answer or not an admin user
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

        if (userAuthTokenEntity.getLogoutAt() != null &&
                userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0 &&
                userAuthTokenEntity.getExpiresAt().compareTo(ZonedDateTime.now()) < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        // Check if Question exists in DB
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null || questionEntity.getUuid().isEmpty()) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        // Get all questions for the given question
        return answerDao.getAllAnswersToQuestion(questionId);
    }
}
