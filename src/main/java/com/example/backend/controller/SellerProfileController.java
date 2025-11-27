package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.service.ISellerProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/seller-profile")
public class SellerProfileController {
    @Autowired
    private ISellerProfileService _sellerProfileService;

    @GetMapping("/active-products")
    public ResponseEntity<?> getActiveProducts(@RequestParam Integer userId) {
        try {
            List<Product> products = _sellerProfileService.getActiveProducts(userId);
            if ((products == null || products.isEmpty())) {
                log.info(
                        "[CONTROLLER][GET][WARN] /api/seller-profile/active-products - No active products found for user with ID: {}",
                        userId);
                return new ResponseEntity<>("No active products found for the given user.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][GET][ERROR] /api/seller-profile/active-products - Error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sold-products")
    public ResponseEntity<?> getSoldProducts(@RequestParam Integer userId) {
        try {
            List<Product> products = _sellerProfileService.getSoldProducts(userId);
            if ((products == null || products.isEmpty())) {
                log.info(
                        "[CONTROLLER][GET][WARN] /api/seller-profile/sold-products - No sold products found for user with ID: {}",
                        userId);
                return new ResponseEntity<>("No sold products found for the user with ID: " + userId,
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][GET][ERROR] /api/seller-profile/sold-products - Error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
