package com.bigone.restaurant.dao;

import com.bigone.restaurant.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category,Integer> {

     List<Category>getAllCategory();
}
