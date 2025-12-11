package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ISellerProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/user-profile")
public class SellerProfileController {
    @Autowired
    private ISellerProfileService _sellerProfileService;

    @GetMapping("/active-products")
    public ResponseEntity<?> getActiveProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Product> products = _sellerProfileService.getActiveProducts(userDetails.getUser().getUserId());

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][GET][ERROR] /api/user-profile/active-products - Error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sold-products")
    public ResponseEntity<?> getSoldProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Product> products = _sellerProfileService.getSoldProducts(userDetails.getUser().getUserId());

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            log.error(
                    "[CONTROLLER][GET][ERROR] /api/user-profile/sold-products - Error occurred: {}", e.getMessage(),
                    e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
