package com.bigone.restaurant.rest;

import com.bigone.restaurant.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {
    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);
    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductWrapper>> getAllProduct();
    @PostMapping(path = "/update")
    public ResponseEntity<String> updateProduct(@RequestBody(required = true) Map<String, String> requestMap);
    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @PostMapping(path = "/updateProductStatus")
    public ResponseEntity<String> updateStatus(@RequestBody(required = true) Map<String, String> requestMap);
}
