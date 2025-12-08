package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.WatchList;
import com.example.backend.model.WatchList.CreateWatchListRequest;
import com.example.backend.service.IWatchListService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/watch-list")
public class WatchListController {

    @Autowired
    private IWatchListService _watchListService;

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
