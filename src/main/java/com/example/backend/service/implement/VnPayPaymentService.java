package com.example.backend.service.implement;

import com.example.backend.config.VnPayConfig;
import com.example.backend.entity.AuctionOrder;
import com.example.backend.repository.IAuctionOrderRepository;
import com.example.backend.service.IPaymentService;
import com.example.backend.utils.VnPayUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayPaymentService implements IPaymentService {
/*
  private final VnPayConfig vnpayConfig;
  private final IAuctionOrderRepository orderRepo;

  @Override
  public Map<String, Object> createPaymentUrl(
      Integer orderId,
      BigDecimal amount,
      String clientIp) {

    Map<String, String> vnpParams = new HashMap<>();

    vnpParams.put("vnp_Version", "2.1.0");
    vnpParams.put("vnp_Command", "pay");
    vnpParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
    vnpParams.put("vnp_Amount",
        amount.multiply(BigDecimal.valueOf(100))
            .toBigInteger().toString());
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_TxnRef", String.valueOf(orderId));
    vnpParams.put("vnp_OrderInfo", "Auction order #" + orderId);
    vnpParams.put("vnp_OrderType", "other");
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
    vnpParams.put("vnp_IpAddr", clientIp);

    // 🔥 TIMEZONE VN
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    String createDate = formatter.format(cld.getTime());
    vnpParams.put("vnp_CreateDate", createDate);

    cld.add(Calendar.MINUTE, 15);
    String expireDate = formatter.format(cld.getTime());
    vnpParams.put("vnp_ExpireDate", expireDate);

    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
      String field = it.next();
      String value = vnpParams.get(field);
      if (value != null && !value.isEmpty()) {

        hashData.append(field).append('=')
            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));

        query.append(URLEncoder.encode(field, StandardCharsets.US_ASCII))
            .append('=')
            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));

        if (it.hasNext()) {
          hashData.append('&');
          query.append('&');
        }
      }
    }

    String secureHash = VnPayUtil.hmacSHA512(
        vnpayConfig.getHashSecret(),
        hashData.toString());

    String paymentUrl = vnpayConfig.getPayUrl()
        + "?" + query
        + "&vnp_SecureHash=" + secureHash;

    return Map.of(
        "code", "00",
        "message", "success",
        "paymentUrl", paymentUrl);
  }

  @Override
  @Transactional
  public Map<String, String> handleIpn(Map<String, String> params) {

    Map<String, String> fields = new HashMap<>(params);

    String secureHash = fields.remove("vnp_SecureHash");
    fields.remove("vnp_SecureHashType");

    String hashData = VnPayUtil.buildHashData(fields);
    String signValue = VnPayUtil.hmacSHA512(
        vnpayConfig.getHashSecret(),
        hashData);

    if (!signValue.equals(secureHash)) {
      return Map.of("RspCode", "97", "Message", "Invalid Checksum");
    }

    Integer orderId = Integer.valueOf(fields.get("vnp_TxnRef"));
    BigDecimal amount = new BigDecimal(fields.get("vnp_Amount"))
        .divide(BigDecimal.valueOf(100));

    AuctionOrder order = orderRepo.findById(orderId).orElse(null);

    if (order == null) {
      return Map.of("RspCode", "01", "Message", "Order not Found");
    }

    if (order.getFinalPrice().compareTo(amount) != 0) {
      return Map.of("RspCode", "04", "Message", "Invalid Amount");
    }

    if (order.getStatus() != AuctionOrder.OrderStatus.WAIT_PAYMENT) {
      return Map.of("RspCode", "02", "Message", "Order already confirmed");
    }

    if ("00".equals(fields.get("vnp_ResponseCode"))) {
      order.setStatus(AuctionOrder.OrderStatus.PAID);
      order.setPaidAt(Instant.now());
      orderRepo.save(order);
    }

    return Map.of("RspCode", "00", "Message", "Confirm Success");
  }*/
}
