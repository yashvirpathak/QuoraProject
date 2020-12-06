package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    // Method to create an Answer
    @RequestMapping(path = "/question/{questionId}/answer/create", method = RequestMethod.POST)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest request, final @PathVariable String questionId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Checking if question exists in DB
        final QuestionEntity questionEntity = questionBusinessService.getQuestionByUuid(questionId);

        // Preparing Answer entity
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(request.getAnswer());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setCreateDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());

        // Creating the answer
        String token = CommonController.getToken(authorizationToken);
        final AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity, token);

        // Preparing and the response
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(createdAnswerEntity.getUuid());
        answerResponse.status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    // Method to edit answer content
    @RequestMapping(path = "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest editRequest, @PathVariable String answerId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity requestAnswerEntity = new AnswerEntity();
        // Add logic to map edit request to Question entity


        // Calling service method to edit answer content
        String token = CommonController.getToken(authorizationToken);
        AnswerEntity responseAnswerEntity = answerBusinessService.editAnswerContent(requestAnswerEntity, token);

        // Preparing and returning response
        AnswerEditResponse response = new AnswerEditResponse();
        response.setId(responseAnswerEntity.getUuid());
        response.setStatus("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(response, HttpStatus.ACCEPTED);
    }

    // Method to delete an answer
    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable String answerId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        // Calling service method to delete the answer
        String token = CommonController.getToken(authorizationToken);
        boolean status = answerBusinessService.deleteAnswer(answerId, token);

        // Preparing and returning response
        AnswerDeleteResponse response = new AnswerDeleteResponse();
        response.setId(answerId);
        response.setStatus("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(response, HttpStatus.OK);
    }

    // Method to get answers for given question
    @RequestMapping(path = "answer/all/{questionId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable String questionId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Initializing response collection
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<AnswerDetailsResponse>();

        // Get question details
        final QuestionEntity questionEntity = questionBusinessService.getQuestionByUuid(questionId);

        // Calling service layer to fetch all answers for the question
        String token = CommonController.getToken(authorizationToken);
        final List<AnswerEntity> answerEntities = answerBusinessService.getAllAnswersToQuestion(questionId, token);

        // Preparing response collection
        for (AnswerEntity answerEntity : answerEntities) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestion().getContent());

            answerDetailsResponses.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}