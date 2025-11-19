package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Product;
import com.example.backend.entity.WatchList;
import com.example.backend.model.WatchList.CreateWatchListRequest;
import com.example.backend.service.IProductService;
import com.example.backend.service.IWatchListService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/watch_list")
public class WatchListController {

    @Autowired
    private IWatchListService _watchListService;
    @Autowired
    private IProductService _productService;

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getWatchListByUserId(@PathVariable("user_id") Integer userId) {

        try {
            List<WatchList> watchLists = _watchListService.getWatchList(userId);

            if (watchLists == null || watchLists.isEmpty()) {
                return new ResponseEntity<>("Watch list of user " + userId + " is empty", HttpStatus.NO_CONTENT);
            }

            List<Product> products = new ArrayList<>();
            for (WatchList watchList : watchLists) {
                Product product = _productService.getProduct(watchList.getProduct().getProductId());
                if (product != null) {
                    products.add(product);
                }
            }
            return new ResponseEntity<>(products, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addToWatchList(@Valid @RequestBody CreateWatchListRequest request) {
        try {
            WatchList addedWatchList = _watchListService.addToWatchList(
                    request.getUserId(),
                    request.getProductId());

            return new ResponseEntity<>(addedWatchList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
