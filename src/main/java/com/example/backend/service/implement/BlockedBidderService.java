package com.example.backend.service.implement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.repository.IBlockedBidderRepository;
import com.example.backend.service.IBlockedBidderService;

@Service
@Slf4j
public class BlockedBidderService implements IBlockedBidderService {

  @Autowired
  private IBlockedBidderRepository _blockedBidderRepository;

  @Override
  public Boolean checkBidderBlocked(Integer productId, Integer bidderId) {
    log.info(
            "[SERVICE][GET][CHECK_BLOCKED_BIDDER] Input productId={}, bidderId={}",
            productId,
            bidderId
    );

    try {
      Boolean blocked =
              _blockedBidderRepository.existsByProductProductIdAndBlockedUserId(
                      productId,
                      bidderId
              );

      log.info(
              "[SERVICE][GET][CHECK_BLOCKED_BIDDER] Output blocked={}",
              blocked
      );
      return blocked;

    } catch (Exception e) {
      log.error(
              "[SERVICE][GET][CHECK_BLOCKED_BIDDER] Error occurred (productId={}, bidderId={}): {}",
              productId,
              bidderId,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
