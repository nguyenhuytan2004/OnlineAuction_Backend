package com.example.backend.service.implement;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.ISellerProfileService;

@Service
@Slf4j
public class SellerProfileService implements ISellerProfileService {

  @Autowired
  private IProductRepository _productRepository;
  @Autowired
  private IAuctionResultRepository _auctionResultRepository;

  @Override
  public List<Product> getActiveProducts(Integer userId) {
    log.info(
            "[SERVICE][GET][SELLER_ACTIVE_PRODUCTS] Input userId={}",
            userId
    );

    try {
      LocalDateTime now = LocalDateTime.now();
      List<Product> products =
              _productRepository
                      .findBySellerUserIdAndEndTimeAfterOrderByEndTimeAsc(userId, now);

      log.info(
              "[SERVICE][GET][SELLER_ACTIVE_PRODUCTS] Output products={}",
              products
      );
      return products;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][SELLER_ACTIVE_PRODUCTS] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }

  @Override
  public List<Product> getSoldProducts(Integer userId) {
    log.info(
            "[SERVICE][GET][SELLER_SOLD_PRODUCTS] Input userId={}",
            userId
    );

    try {
      List<Product> products =
              _auctionResultRepository.findSoldProductsBySellerUserId(userId);

      log.info(
              "[SERVICE][GET][SELLER_SOLD_PRODUCTS] Output products={}",
              products
      );
      return products;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][SELLER_SOLD_PRODUCTS] Error occurred (userId={}): {}",
              userId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
