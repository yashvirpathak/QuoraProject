package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        String token = CommonController.getToken(authorizationToken);
        adminBusinessService.userDelete(userId, token);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse();
        userDeleteResponse.setId(userId);
        userDeleteResponse.setStatus("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
