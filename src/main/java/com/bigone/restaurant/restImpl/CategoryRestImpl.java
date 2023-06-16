package com.bigone.restaurant.restImpl;

import com.bigone.restaurant.POJO.Category;
import com.bigone.restaurant.constents.RestaurantConstants;
import com.bigone.restaurant.dao.CategoryDao;
import com.bigone.restaurant.rest.CategoryRest;
import com.bigone.restaurant.service.CategoryService;
import com.bigone.restaurant.utils.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CategoryRestImpl implements CategoryRest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDao categoryDao;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
       try {
           //System.out.println("inside userRestImpl");
          return categoryService.addNewCategory(requestMap);
       }catch (Exception ex){
           ex.printStackTrace();
       }
        //System.out.println("Before return");
       return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String Value) {
        try{
          return categoryService.getAllCategory(Value);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            return categoryService.update(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
