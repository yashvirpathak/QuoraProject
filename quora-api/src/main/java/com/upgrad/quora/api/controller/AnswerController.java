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
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(path = "/question/{questionId}/answer/create", method = RequestMethod.POST)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest request, final @PathVariable String questionId, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException
    {
        final QuestionEntity questionEntity=questionBusinessService.getQuestionByUuid(questionId);

        AnswerEntity answerEntity=new AnswerEntity();
        answerEntity.setAnswer(request.getAnswer());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setCreateDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());

        final AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity, authorizationToken);

        AnswerResponse answerResponse=new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest editRequest, @PathVariable String answerId, final String authorizationToken )
            throws AuthorizationFailedException, AnswerNotFoundException
    {

        AnswerEntity requestAnswerEntity=new AnswerEntity();
        // Add logic to map edit request to Question entity


        AnswerEntity responseAnswerEntity = answerBusinessService.editAnswerContent(requestAnswerEntity,authorizationToken);

        AnswerEditResponse response=new AnswerEditResponse();
        response.setId(responseAnswerEntity.getUuid());
        response.setStatus("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable String answerId, final String authorizationToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        boolean status = answerBusinessService.deleteAnswer(answerId, authorizationToken);
        AnswerDeleteResponse response=new AnswerDeleteResponse();
        response.setId(answerId);
        response.setStatus("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "answer/all/{questionId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable String questionId,  final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException
    {
        List<AnswerDetailsResponse> answerDetailsResponses=new ArrayList<AnswerDetailsResponse>();
        final QuestionEntity questionEntity=questionBusinessService.getQuestionByUuid(questionId);

        final List<AnswerEntity> answerEntities=answerBusinessService.getAllAnswersToQuestion(questionId,authorizationToken);

        for(AnswerEntity answerEntity: answerEntities){
            AnswerDetailsResponse answerDetailsResponse= new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestion().getContent());

            answerDetailsResponses.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}
