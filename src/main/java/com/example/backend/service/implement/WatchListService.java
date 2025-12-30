package com.example.backend.service.implement;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.entity.WatchList;
import com.example.backend.repository.IWatchListRepository;
import com.example.backend.service.IProductService;
import com.example.backend.service.IUserService;
import com.example.backend.service.IWatchListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WatchListService implements IWatchListService {

  @Autowired
  private IWatchListRepository _watchListRepository;
  @Autowired
  private IUserService _userService;
  @Autowired
  private IProductService _productService;

  @Override
  public List<WatchList> getWatchList(Integer userId) {

    log.info(
            "[SERVICE][GET][WATCH_LIST] Input userId={}",
            userId
    );

    try {
      List<WatchList> watchLists =
              _watchListRepository.findByUserUserId(userId);

      log.info(
              "[SERVICE][GET][WATCH_LIST] Output watchLists={}",
              watchLists
      );

      return watchLists;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][WATCH_LIST] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public boolean isInWatchList(Integer userId, Integer productId) {

    log.info(
            "[SERVICE][GET][IS_IN_WATCH_LIST] Input userId={}, productId={}",
            userId,
            productId
    );

    try {
      WatchList watchList =
              _watchListRepository
                      .findByUserUserIdAndProductProductId(userId, productId);

      boolean result = watchList != null;

      log.info(
              "[SERVICE][GET][IS_IN_WATCH_LIST] Output result={}",
              result
      );

      return result;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][IS_IN_WATCH_LIST] Error occurred (userId={}, productId={}): {}",
              userId,
              productId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public WatchList addToWatchList(Integer userId, Integer productId) {

    log.info(
            "[SERVICE][POST][ADD_WATCH_LIST] Input userId={}, productId={}",
            userId,
            productId
    );

    try {
      User user = _userService.getUser(userId);
      Product product = _productService.getProduct(productId);

      WatchList watchList = new WatchList();
      watchList.setUser(user);
      watchList.setProduct(product);

      WatchList saved =
              _watchListRepository.save(watchList);

      log.info(
              "[SERVICE][POST][ADD_WATCH_LIST] Success watchListId={}",
              saved.getWatchListId()
      );

      return saved;

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][ADD_WATCH_LIST] Error occurred (userId={}, productId={}): {}",
              userId,
              productId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public WatchList removeFromWatchList(Integer userId, Integer productId) {

    log.info(
            "[SERVICE][DELETE][REMOVE_WATCH_LIST] Input userId={}, productId={}",
            userId,
            productId
    );

    try {
      WatchList watchList =
              _watchListRepository
                      .findByUserUserIdAndProductProductId(userId, productId);

      if (watchList != null) {
        _watchListRepository.delete(watchList);

        log.info(
                "[SERVICE][DELETE][REMOVE_WATCH_LIST] Success userId={}, productId={}",
                userId,
                productId
        );

        return watchList;
      }

      log.info(
              "[SERVICE][DELETE][REMOVE_WATCH_LIST] Not found userId={}, productId={}",
              userId,
              productId
      );

      return null;

    } catch (Exception e) {
      log.error(
              "[SERVICE][DELETE][REMOVE_WATCH_LIST] Error occurred (userId={}, productId={}): {}",
              userId,
              productId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}

