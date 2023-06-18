package com.bigone.restaurant.restImpl;

import com.bigone.restaurant.constents.RestaurantConstants;
import com.bigone.restaurant.rest.UserRest;
import com.bigone.restaurant.service.UserService;
import com.bigone.restaurant.utils.RestaurantUtils;
import com.bigone.restaurant.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class UserRestImpl implements UserRest {
@Autowired
    UserService userService;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
       try {
           //System.out.println("inside userRestImpl");
         return  userService.signUp(requestMap);
       }catch (Exception ex){
           ex.printStackTrace();
       }
        //System.out.println("Before return");
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
      try{
          return userService.login(requestMap);
      }catch (Exception ex){
          ex.printStackTrace();
      }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
        return  userService.getAllUser();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
         return userService.update(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
       try{
           return userService.checkToken();
       }catch (Exception ex){
           ex.printStackTrace();
       }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            return userService.changePassword(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try{
         return userService.forgetPassword(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
