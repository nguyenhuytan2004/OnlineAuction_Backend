package com.example.backend.service.implement;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.backend.entity.AuctionOrder;
import com.example.backend.entity.AuctionOrder.OrderStatus;
import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Bid;
import com.example.backend.entity.Product;
import com.example.backend.model.AuctionOrder.CancelOrderRequest;
import com.example.backend.model.AuctionOrder.OrderStatusResponse;
import com.example.backend.model.AuctionOrder.PayOrderRequest;
import com.example.backend.model.AuctionOrder.PayOrderResponse;
import com.example.backend.model.AuctionOrder.SetShippingAddressRequest;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.IBidRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.service.IOrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

  private final IAuctionOrderRepository orderRepo;
  private final IProductRepository productRepo;
  private final IBidRepository _bidRepository;
  private final IAuctionResultRepository _auctionResultRepository;

  @Override
  public AuctionOrder getAuctionOrderByProductId(Integer productId) {
    return orderRepo.findByProductProductId(productId)
        .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại cho sản phẩm này"));
  }

  @Override
  public void createAuctionOrder(Product product) {
    AuctionOrder existing = orderRepo.findByProductProductId(product.getProductId()).orElse(null);
    if (existing != null) {
      return;
    }

    Bid highestBid = _bidRepository.findTopByProductProductIdOrderByBidPriceDesc(product.getProductId());
    if (highestBid != null) {
      AuctionOrder order = new AuctionOrder();
      order.setProduct(product);
      order.setSeller(product.getSeller());
      order.setBuyer(highestBid.getBidder());
      order.setFinalPrice(product.getCurrentPrice());
      order.setStatus(OrderStatus.WAIT_PAYMENT);
      orderRepo.save(order);
    }

    // Đã thực hiện trong AuctionService.updateAuctionResult
    // product.setIsActive(false);
    // _productRepository.save(product);
  }

  @Override
  @Transactional
  public PayOrderResponse payAndCreateOrder(PayOrderRequest req) {

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
        return new PayOrderResponse(
            existing.getOrderId(),
            existing.getStatus());
      }

      existing.setPaidAt(Instant.now());
      existing.setStatus(OrderStatus.PAID);

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

    return new PayOrderResponse(order.getOrderId(), order.getStatus());
  }

  @Override
  @Transactional
  public OrderStatusResponse getStatus(Integer orderId) {
    var order = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

    return new OrderStatusResponse(
        order.getOrderId(),
        order.getStatus(),
        order.getShippingAddress() != null && !order.getShippingAddress().isBlank(),
        order.getFinalPrice());
  }

  @Override
  @Transactional
  public void setShippingAddress(Integer orderId, SetShippingAddressRequest req) {
    var order = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

    if (order.getStatus() != OrderStatus.PAID)
      throw new RuntimeException("Bạn chưa thanh toán đơn hàng");

    order.setShippingAddress(req.getShippingAddress());
  }

  @Override
  @Transactional
  public void sellerConfirmPayment(Integer orderId) {
    var order = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

    if (order.getStatus() != OrderStatus.PAID)
      throw new RuntimeException("Người mua chưa thanh toán đơn hàng");

    if (order.getShippingAddress() == null || order.getShippingAddress().isBlank())
      throw new RuntimeException("Người mua chưa thiết lập địa chỉ giao hàng");

    order.setStatus(OrderStatus.ON_DELIVERING);
  }

  @Override
  @Transactional
  public void buyerConfirmReceived(Integer orderId) {
    var order = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

    if (order.getStatus() != OrderStatus.ON_DELIVERING)
      throw new RuntimeException("Chỉ có thể xác nhận nhận hàng khi đơn hàng đang trong trạng thái Đang giao");

    order.setStatus(OrderStatus.COMPLETED);
  }

  @Override
  @Transactional
  public void cancel(Integer orderId, CancelOrderRequest req) {
    var orderOption = orderRepo.findById(orderId);

    AuctionOrder order = orderOption.get();

    if (order.getStatus() == OrderStatus.COMPLETED ||
        order.getStatus() == OrderStatus.CANCELED)
      throw new RuntimeException("Không thể hủy đơn hàng đã hoàn thành hoặc đã hủy");

    order.setStatus(OrderStatus.CANCELED);
    order.setCanceledReason(req.getReason());
  }
}
