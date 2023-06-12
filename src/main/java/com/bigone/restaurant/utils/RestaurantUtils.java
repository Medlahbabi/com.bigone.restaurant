package com.bigone.restaurant.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestaurantUtils {


    public RestaurantUtils() {
    }

    public static ResponseEntity<String> getResponseEntity(String reponseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"message\":\"" + reponseMessage + "\"}", httpStatus);
    }

    public static String getUUID() {
        Date data = new Date();
        long time = data.getTime();
        return "BILL" + time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
            }.getType());
        return new HashMap<>();
    }
}