package com.bigone.restaurant.serviceImpl;

import com.bigone.restaurant.JWT.JwtFilter;
import com.bigone.restaurant.POJO.Category;
import com.bigone.restaurant.constents.RestaurantConstants;
import com.bigone.restaurant.dao.CategoryDao;
import com.bigone.restaurant.service.CategoryService;
import com.bigone.restaurant.utils.RestaurantUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
               if (validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap , false));
                   return RestaurantUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
               }
            } else {
               return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
           ex.printStackTrace();
        }
        //System.out.println(RestaurantConstants.SOMETHING_WENT_WRONG);
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String Value) {
        try {
            if(!Strings.isNullOrEmpty(Value) && Value.equalsIgnoreCase("true")) {
                return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.OK);

            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap , true)) {

                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));

                    if (!optional.isEmpty()) {
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return RestaurantUtils.getResponseEntity("Category is updated successfully", HttpStatus.OK);

                    } else {
                        return RestaurantUtils.getResponseEntity("Category id doesn't exist", HttpStatus.OK);
                    }

                }
                return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
     if (requestMap.containsKey("name")){
         if (requestMap.containsKey("id")&& validateId){
             return true;
         }else if(!validateId){
             return true;
         }
     }
        return false;
    }
    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd){
        Category category= new Category();
        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }
}
