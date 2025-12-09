package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.WatchList;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.IWatchListService;

@RestController
@RequestMapping("/api/watch-list")
public class WatchListController {

    @Autowired
    private IWatchListService _watchListService;

    @GetMapping("{productId}")
    public ResponseEntity<?> isInWatchList(@PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isInWatchList = _watchListService.isInWatchList(
                    userDetails.getUser().getUserId(),
                    productId);

            return new ResponseEntity<>(isInWatchList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{productId}")
    public ResponseEntity<?> addToWatchList(@PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            WatchList addedWatchList = _watchListService.addToWatchList(
                    userDetails.getUser().getUserId(),
                    productId);

            return new ResponseEntity<>(addedWatchList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<?> removeFromWatchList(@PathVariable Integer productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            WatchList removedWatchList = _watchListService.removeFromWatchList(userDetails.getUser().getUserId(),
                    productId);

            return new ResponseEntity<>(removedWatchList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}