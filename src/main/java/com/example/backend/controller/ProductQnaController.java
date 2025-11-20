package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.IProductQnaService;

@RestController
@RequestMapping("api/product-qnas")
public class ProductQnaController {
    @Autowired
    private IProductQnaService _productQnaService;

    @GetMapping("product/{product_Id}")
    public ResponseEntity<?> getProductQnaByProductId(@PathVariable("product_Id") Integer productId) {
        try {
            List<?> qnas = _productQnaService.getProductQnaByProductId(productId);
            if (qnas == null || qnas.isEmpty()) {
                return new ResponseEntity<>("No QnA found for product with ID: " + productId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(qnas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
