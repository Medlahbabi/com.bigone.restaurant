package com.bigone.restaurant.service;

import com.bigone.restaurant.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);
    ResponseEntity<List<Category>> getAllCategory(String Value);
    ResponseEntity<String> update(Map<String, String> requestMap);

}
