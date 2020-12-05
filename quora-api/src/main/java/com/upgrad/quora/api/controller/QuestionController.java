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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(path = "/question/create", method = RequestMethod.POST)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest request) throws AuthorizationFailedException {
        final QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setCreateDate(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity, "token");

        QuestionResponse questionResponse=new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions() throws AuthorizationFailedException {

        List<QuestionDetailsResponse> questionDetailsResponses=new ArrayList<QuestionDetailsResponse>();

        final List<QuestionEntity> questionEntities=questionBusinessService.getAllQuestions("token");

        for(QuestionEntity questionEntity: questionEntities){
            questionDetailsResponses.add(
                    new QuestionDetailsResponse()
                            .id(questionEntity.getUuid())
                            .content(questionEntity.getContent())
            );
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/edit/{questionId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest editRequest, @PathVariable String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity requestQuestionEntity=new QuestionEntity();
        // Add logic to map edit request to Question entity


        QuestionEntity questionResponseEntity = questionBusinessService.editQuestionContent(requestQuestionEntity,"token");

        QuestionEditResponse response=new QuestionEditResponse();
        response.setId(questionResponseEntity.getUuid());
        response.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response, HttpStatus.ACCEPTED);

    }

    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        questionBusinessService.deleteQuestion(questionId,"token");
        return new ResponseEntity<QuestionDeleteResponse>(HttpStatus.OK);
    }

    @RequestMapping(path = "question/all/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable String userId)
            throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> questionDetailsResponses=new ArrayList<QuestionDetailsResponse>();

        final List<QuestionEntity> questionEntities=questionBusinessService.getAllQuestionsByUser(userId,"token");

        for(QuestionEntity questionEntity: questionEntities){
            questionDetailsResponses.add(
                    new QuestionDetailsResponse()
                            .id(questionEntity.getUuid())
                            .content(questionEntity.getContent())
            );
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
}