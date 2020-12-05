package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {
    @Autowired
    private AdminBusinessService adminBusinessService;

    @RequestMapping(path = "/admin/user/{userId}", method = RequestMethod.POST)
    public ResponseEntity userDelete(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException {


        return new ResponseEntity("USER SUCCESSFULLY DELETED", HttpStatus.OK);
    }
}
