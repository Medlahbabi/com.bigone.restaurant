package com.bigone.restaurant.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestaurantUtils {
    public RestaurantUtils() {
    }
    public static ResponseEntity<String > getResponseEntity(String reponseMessage , HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+reponseMessage+"\"}",httpStatus);
    }
}
