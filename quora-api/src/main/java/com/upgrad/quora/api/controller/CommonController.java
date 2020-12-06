package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userDetails(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        // Calling user service to get user details
        String token=getToken(authorization);
        UserEntity userEntity = userBusinessService.getUser(userId, token);

        // Preparing and returning user details
        UserDetailsResponse userDetails = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber()).userName(userEntity.getUserName())
                .country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob());

        return new ResponseEntity<UserDetailsResponse>(userDetails, HttpStatus.OK);
    }

    // Method to get token
    public static String getToken(final String token) {
        String[] userAuthorizationToken = token.split("Bearer ");
        return userAuthorizationToken[1];
    }
}
