package com.example.backend.service.implement;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.PaymentTransaction;
import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.model.PayOS.CreatePayOSPaymentLinkRequest;
import com.example.backend.repository.IPaymentTransactionRepository;
import com.example.backend.repository.ISellerUpgradeRequestRepository;
import com.example.backend.repository.IUserRepository;

import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

@Service
public class PayOSPaymentService {
  @Autowired
  private PayOS payOS;

  @Autowired
  private IPaymentTransactionRepository _paymentTransactionRepository;
  @Autowired
  private ISellerUpgradeRequestRepository _sellerUpgradeRequestRepository;
  @Autowired
  private IUserRepository _userRepository;

  public CreatePaymentLinkResponse createPayOSPaymentLink(CreatePayOSPaymentLinkRequest createPayOSPaymentLinkRequest) {
    // 1 Lấy dữ liệu từ request body
    final String productName = createPayOSPaymentLinkRequest.getOrderName();
    final String description = createPayOSPaymentLinkRequest.getDescription();
    final String returnUrl = createPayOSPaymentLinkRequest.getReturnUrl();
    final String cancelUrl = createPayOSPaymentLinkRequest.getCancelUrl();
    final long price = createPayOSPaymentLinkRequest.getAmount().longValue();

    // 2 Tạo mã đơn hàng
    long orderCode = System.currentTimeMillis() / 1000;

    PaymentLinkItem item = PaymentLinkItem.builder()
        .name(productName)
        .quantity(1)
        .price(price)
        .build();

    // 3 Tạo request payment link
    CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
        .orderCode(orderCode)
        .description(description)
        .amount(price)
        .item(item)
        .returnUrl(returnUrl)
        .cancelUrl(cancelUrl)
        .build();

    // 4 Gửi tới PayOS
    CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

    // 5 Lưu thông tin giao dịch vào database
    PaymentTransaction paymentTransaction = new PaymentTransaction();
    paymentTransaction.setOrderCode(orderCode);
    paymentTransaction.setUserId(createPayOSPaymentLinkRequest.getUserId());
    paymentTransaction.setAmount(createPayOSPaymentLinkRequest.getAmount());
    paymentTransaction.setType(createPayOSPaymentLinkRequest.getType());
    paymentTransaction.setStatus(PaymentTransaction.TransactionStatus.PENDING);
    paymentTransaction.setProductId(createPayOSPaymentLinkRequest.getProductId());
    _paymentTransactionRepository.save(paymentTransaction);

    return data;
  }

  public WebhookData handlePayOSWebhook(Object body) {
    // 1 PayOS gửi webhook với dữ liệu thanh toán
    System.out.println("Webhook received from PayOS:");
    System.out.println(body);

    // 2 Xác minh chữ ký webhook (đảm bảo từ PayOS)
    WebhookData data = payOS.webhooks().verify(body);

    // Verifying successful
    System.out.println("Webhook verified successfully:");
    System.out.println(data);

    // 3 Xử lý dữ liệu webhook (Cập nhật yêu cầu nâng cấp SELLER, Trạng thái đơn
    // hàng, v.v.)
    PaymentTransaction transaction = _paymentTransactionRepository.findByOrderCode(data.getOrderCode());

    if (transaction.getStatus() == PaymentTransaction.TransactionStatus.COMPLETED) {
      return data; // Đã xử lý rồi thì thoát luôn, không tạo Request mới nữa
    }

    if ("00".equals(data.getCode())) {
      if (transaction.getType() == PaymentTransaction.TransactionType.UPGRADE_SELLER) {
        SellerUpgradeRequest upgradeRequest = new SellerUpgradeRequest();
        upgradeRequest.setUser(_userRepository.findByUserId(transaction.getUserId()));
        upgradeRequest.setStatus(SellerUpgradeRequest.Status.PENDING);
        upgradeRequest.setRequestAt(LocalDateTime.now());
        _sellerUpgradeRequestRepository.save(upgradeRequest);
      }
      transaction.setStatus(PaymentTransaction.TransactionStatus.COMPLETED);
      _paymentTransactionRepository.save(transaction);
    }

    return data;
  }
}
