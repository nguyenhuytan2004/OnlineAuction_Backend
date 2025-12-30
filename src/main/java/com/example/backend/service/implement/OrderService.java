package com.example.backend.service.implement;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionOrder.OrderStatus;
import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.model.AuctionOrder.*;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService implements IOrderService {

  private final IAuctionOrderRepository orderRepo;
  private final IProductRepository productRepo;
  private final IBidRepository _bidRepository;
  private final IAuctionResultRepository _auctionResultRepository;

  @Override
  public AuctionOrder getAuctionOrderByProductId(Integer productId) {
    log.info(
        "[SERVICE][GET][AUCTION_ORDER_BY_PRODUCT] Input productId={}",
        productId);

    try {
      AuctionOrder order = orderRepo.findByProductProductId(productId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại cho sản phẩm này"));

      log.info(
          "[SERVICE][GET][AUCTION_ORDER_BY_PRODUCT] Output order={}",
          order);
      return order;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][AUCTION_ORDER_BY_PRODUCT] Error occurred (productId={}): {}",
          productId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  public void createAuctionOrder(Product product) {
    log.info(
        "[SERVICE][POST][CREATE_AUCTION_ORDER] Input productId={}",
        product.getProductId());

    try {
      AuctionOrder existing = orderRepo.findByProductProductId(product.getProductId()).orElse(null);
      if (existing != null) {
        log.info(
            "[SERVICE][POST][CREATE_AUCTION_ORDER] Skip (already exists) productId={}",
            product.getProductId());
        return;
      }

      Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(
          product.getProductId());

      if (highestBid != null) {
        AuctionOrder order = new AuctionOrder();
        order.setProduct(product);
        order.setSeller(product.getSeller());
        order.setBuyer(highestBid.getBidder());
        order.setFinalPrice(product.getCurrentPrice());
        order.setStatus(OrderStatus.WAIT_PAYMENT);
        orderRepo.save(order);

        log.info(
            "[SERVICE][POST][CREATE_AUCTION_ORDER] Created order productId={}, buyerId={}",
            product.getProductId(),
            highestBid.getBidder().getUserId());
      }

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][CREATE_AUCTION_ORDER] Error occurred (productId={}): {}",
          product.getProductId(),
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public PayOrderResponse payAndCreateOrder(PayOrderRequest req) {
    log.info(
        "[SERVICE][POST][PAY_ORDER] Input request={}",
        req);

    try {
      Product product = productRepo.findById(req.getProductId())
          .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

      if (product.getHighestBidder() == null) {
        throw new RuntimeException("Không có người đấu giá sản phẩm này");
      }

      BigDecimal finalPrice = product.getCurrentPrice();
      if (req.getAmount().compareTo(finalPrice) != 0) {
        throw new RuntimeException("Số tiền thanh toán không khớp với giá cuối cùng");
      }

      AuctionOrder existing = orderRepo.findByProductProductId(product.getProductId()).orElse(null);

      if (existing != null) {

        if (existing.getStatus() != OrderStatus.WAIT_PAYMENT) {
          log.info(
              "[SERVICE][POST][PAY_ORDER] Skip pay orderId={}, status={}",
              existing.getOrderId(),
              existing.getStatus());
          return new PayOrderResponse(
              existing.getOrderId(),
              existing.getStatus());
        }

        existing.setPaidAt(Instant.now());
        existing.setStatus(OrderStatus.PAID);

        log.info(
            "[SERVICE][POST][PAY_ORDER] Paid existing orderId={}",
            existing.getOrderId());

        return new PayOrderResponse(
            existing.getOrderId(),
            existing.getStatus());
      }

      AuctionOrder order = new AuctionOrder();
      order.setProduct(product);
      order.setSeller(product.getSeller());
      order.setBuyer(product.getHighestBidder());
      order.setFinalPrice(finalPrice);
      order.setPaidAt(Instant.now());
      order.setStatus(OrderStatus.PAID);

      orderRepo.save(order);

      AuctionResult auctionResult = _auctionResultRepository
          .findByProductProductId(product.getProductId());
      auctionResult.setPaymentStatus(AuctionResult.PaymentStatus.PAID);

      _auctionResultRepository.save(auctionResult);

      log.info(
          "[SERVICE][POST][PAY_ORDER] Created & paid new orderId={}",
          order.getOrderId());

      return new PayOrderResponse(
          order.getOrderId(),
          order.getStatus());

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][PAY_ORDER] Error occurred (request={}): {}",
          req,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public OrderStatusResponse getStatus(Integer orderId) {
    log.info(
        "[SERVICE][GET][ORDER_STATUS] Input orderId={}",
        orderId);

    try {
      var order = orderRepo.findById(orderId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

      OrderStatusResponse res = new OrderStatusResponse(
          order.getOrderId(),
          order.getStatus(),
          order.getShippingAddress() != null && !order.getShippingAddress().isBlank(),
          order.getFinalPrice());

      log.info(
          "[SERVICE][GET][ORDER_STATUS] Output status={}",
          res);
      return res;

    } catch (Exception e) {
      log.error(
          "[SERVICE][GET][ORDER_STATUS] Error occurred (orderId={}): {}",
          orderId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void setShippingAddress(Integer orderId, SetShippingAddressRequest req) {
    log.info(
        "[SERVICE][PUT][SET_SHIPPING_ADDRESS] Input orderId={}, request={}",
        orderId,
        req);

    try {
      var order = orderRepo.findById(orderId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

      if (order.getStatus() != OrderStatus.PAID)
        throw new RuntimeException("Bạn chưa thanh toán đơn hàng");

      order.setShippingAddress(req.getShippingAddress());

      log.info(
          "[SERVICE][PUT][SET_SHIPPING_ADDRESS] Success orderId={}",
          orderId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][PUT][SET_SHIPPING_ADDRESS] Error occurred (orderId={}): {}",
          orderId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void sellerConfirmPayment(Integer orderId) {
    log.info(
        "[SERVICE][POST][SELLER_CONFIRM_PAYMENT] Input orderId={}",
        orderId);

    try {
      var order = orderRepo.findById(orderId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

      if (order.getStatus() != OrderStatus.PAID)
        throw new RuntimeException("Người mua chưa thanh toán đơn hàng");

      if (order.getShippingAddress() == null || order.getShippingAddress().isBlank())
        throw new RuntimeException("Người mua chưa thiết lập địa chỉ giao hàng");

      order.setStatus(OrderStatus.ON_DELIVERING);

      log.info(
          "[SERVICE][POST][SELLER_CONFIRM_PAYMENT] Success orderId={}",
          orderId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][SELLER_CONFIRM_PAYMENT] Error occurred (orderId={}): {}",
          orderId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void buyerConfirmReceived(Integer orderId) {
    log.info(
        "[SERVICE][POST][BUYER_CONFIRM_RECEIVED] Input orderId={}",
        orderId);

    try {
      var order = orderRepo.findById(orderId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

      if (order.getStatus() != OrderStatus.ON_DELIVERING)
        throw new RuntimeException(
            "Chỉ có thể xác nhận nhận hàng khi đơn hàng đang trong trạng thái Đang giao");

      order.setStatus(OrderStatus.COMPLETED);

      log.info(
          "[SERVICE][POST][BUYER_CONFIRM_RECEIVED] Success orderId={}",
          orderId);

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][BUYER_CONFIRM_RECEIVED] Error occurred (orderId={}): {}",
          orderId,
          e.getMessage(),
          e);
      throw e;
    }
  }

  @Override
  @Transactional
  public void cancel(Integer orderId, CancelOrderRequest req) {
    log.info(
        "[SERVICE][POST][CANCEL_ORDER] Input orderId={}, request={}",
        orderId,
        req);

    try {
      var order = orderRepo.findById(orderId)
          .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

      if (order.getStatus() == OrderStatus.COMPLETED ||
          order.getStatus() == OrderStatus.CANCELLED)
        throw new RuntimeException("Không thể hủy đơn hàng đã hoàn thành hoặc đã hủy");

      order.setStatus(OrderStatus.CANCELLED);
      order.setCancelledReason(req.getReason());

      log.info(
          "[SERVICE][POST][CANCEL_ORDER] Success orderId={}, reason={}",
          orderId,
          req.getReason());

    } catch (Exception e) {
      log.error(
          "[SERVICE][POST][CANCEL_ORDER] Error occurred (orderId={}): {}",
          orderId,
          e.getMessage(),
          e);
      throw e;
    }
  }
}
