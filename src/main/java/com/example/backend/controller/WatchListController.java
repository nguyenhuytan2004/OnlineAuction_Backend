package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class WatchListController {

  private final IWatchListService _watchListService;

  @GetMapping("{productId}")
  public ResponseEntity<?> isInWatchList(
          @PathVariable Integer productId,
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      boolean isInWatchList =
              _watchListService.isInWatchList(userId, productId);

      return new ResponseEntity<>(isInWatchList, HttpStatus.OK);

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][GET][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
              productId, userId, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @PostMapping("{productId}")
  public ResponseEntity<?> addToWatchList(
          @PathVariable Integer productId,
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      WatchList addedWatchList =
              _watchListService.addToWatchList(userId, productId);

      log.info(
              "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Added to watch list (userId={})",
              productId, userId
      );

      return new ResponseEntity<>(addedWatchList, HttpStatus.CREATED);

    } catch (IllegalArgumentException iae) {
      log.warn(
              "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Invalid request (userId={}): {}",
              productId, userId, iae.getMessage()
      );
      return new ResponseEntity<>(
              iae.getMessage(),
              HttpStatus.BAD_REQUEST
      );

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][POST][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
              productId, userId, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @DeleteMapping("{productId}")
  public ResponseEntity<?> removeFromWatchList(
          @PathVariable Integer productId,
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    Integer userId = userDetails.getUser().getUserId();

    try {
      WatchList removedWatchList =
              _watchListService.removeFromWatchList(userId, productId);

      log.info(
              "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Removed from watch list (userId={})",
              productId, userId
      );

      return new ResponseEntity<>(removedWatchList, HttpStatus.OK);

    } catch (IllegalArgumentException iae) {
      log.warn(
              "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Invalid request (userId={}): {}",
              productId, userId, iae.getMessage()
      );
      return new ResponseEntity<>(
              iae.getMessage(),
              HttpStatus.BAD_REQUEST
      );

    } catch (Exception e) {
      log.error(
              "[CONTROLLER][DELETE][WATCH_LIST] /api/watch-list/{} - Error occurred (userId={}): {}",
              productId, userId, e.getMessage(), e
      );
      return new ResponseEntity<>(
              "Internal server error: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }
}
