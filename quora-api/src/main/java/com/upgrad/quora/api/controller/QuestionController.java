package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    // Method to create question
    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest request, @RequestHeader("Authorization") final String authorizationToken)
            throws AuthorizationFailedException {

        // Initializing Question Entity
        // abc - 2
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setCreateDate(ZonedDateTime.now());

        // Creating question
        String token = CommonController.getToken(authorizationToken);
        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity, token);

        // Preparing response and returning data
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    // Method to get all questions
    @RequestMapping(path = "/question/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("Authorization") final String authorizationToken)
            throws AuthorizationFailedException {

        // Initiating the collection
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        // get all questions
        String token = CommonController.getToken(authorizationToken);
        final List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestions(token);

        // Adding questions in the collection
        for (QuestionEntity questionEntity : questionEntities) {
            questionDetailsResponses.add(
                    new QuestionDetailsResponse()
                            .id(questionEntity.getUuid())
                            .content(questionEntity.getContent())
            );
        }

        // returning the response data
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }

    // Method to edit/update question content
    @RequestMapping(path = "/question/edit/{questionId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest editRequest, @PathVariable String questionId, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity requestQuestionEntity = new QuestionEntity();
        // Add logic to map edit request to Question entity


        // Method to edit the question content
        QuestionEntity questionResponseEntity = questionBusinessService.editQuestionContent(requestQuestionEntity, authorizationToken);

        // Preparing and returning response
        QuestionEditResponse response = new QuestionEditResponse();
        response.setId(questionResponseEntity.getUuid());
        response.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response, HttpStatus.ACCEPTED);

    }

    // Method to delete the question
    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable String questionId, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Calling delete method in question business service
        questionBusinessService.deleteQuestion(questionId, authorizationToken);

        // returning response
        return new ResponseEntity<QuestionDeleteResponse>(HttpStatus.OK);
    }

    // Method to get all Questions owned by user
    @RequestMapping(path = "question/all/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable String userId, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        // Calling service to get all questions by user
        final List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestionsByUser(userId, authorizationToken);

        // Adding all questions in the collection
        for (QuestionEntity questionEntity : questionEntities) {
            questionDetailsResponses.add(
                    new QuestionDetailsResponse()
                            .id(questionEntity.getUuid())
                            .content(questionEntity.getContent())
            );
        }

        // returning the collection
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
}