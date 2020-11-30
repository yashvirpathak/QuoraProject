package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(path = "/question/create", method = RequestMethod.POST)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest request){
        final QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setCreateDate(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity);

        QuestionResponse questionResponse=new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllQuestions(){
        return new ResponseEntity<QuestionResponse>(HttpStatus.OK);
    }

    @RequestMapping(path = "/question/edit/{questionId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest editRequest, @PathVariable String questionId){
        return new ResponseEntity<QuestionEditResponse>(HttpStatus.ACCEPTED);

    }

    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable String questionId){

        return new ResponseEntity<QuestionDeleteResponse>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "question/all/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(@PathVariable String userId){
        return new ResponseEntity<QuestionDetailsResponse>(HttpStatus.OK);
    }
}
