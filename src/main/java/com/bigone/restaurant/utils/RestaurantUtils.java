package com.bigone.restaurant.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class RestaurantUtils {




    public RestaurantUtils() {
    }
    public static ResponseEntity<String > getResponseEntity(String reponseMessage , HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+reponseMessage+"\"}",httpStatus);
    }
    public static String getUUID(){
        Date data = new Date();
        long time =  data.getTime();
        return "BILL" + time;
    }
}
