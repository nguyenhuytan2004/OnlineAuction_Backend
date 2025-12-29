package com.example.backend.service.implement;

import com.example.backend.config.MomoProperties;
import com.example.backend.model.Momo.MomoCreateResponse;
import com.example.backend.utils.MomoSignatureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MomoPaymentService {

  private final MomoProperties momoProperties;
  private final WebClient webClient = WebClient.builder().build();

  public String createPayUrl(Long amount, String orderInfo) {

    log.info(
            "[SERVICE][POST][MOMO_CREATE_PAY_URL] Input amount={}, orderInfo={}",
            amount,
            orderInfo
    );

    try {
      String requestId = UUID.randomUUID().toString();
      String orderId = "ORDER_" + System.currentTimeMillis();
      String extraData = "";

      String rawSignature =
              "accessKey=" + momoProperties.getAccessKey() +
                      "&amount=" + amount +
                      "&extraData=" + extraData +
                      "&ipnUrl=" + momoProperties.getIpnUrl() +
                      "&orderId=" + orderId +
                      "&orderInfo=" + orderInfo +
                      "&partnerCode=" + momoProperties.getPartnerCode() +
                      "&redirectUrl=" + momoProperties.getRedirectUrl() +
                      "&requestId=" + requestId +
                      "&requestType=captureWallet";

      String signature = MomoSignatureUtil.hmacSHA256(
              momoProperties.getSecretKey(),
              rawSignature
      );

      Map<String, Object> body = new HashMap<>();
      body.put("partnerCode", momoProperties.getPartnerCode());
      body.put("requestId", requestId);
      body.put("orderId", orderId);
      body.put("amount", amount);
      body.put("orderInfo", orderInfo);
      body.put("redirectUrl", momoProperties.getRedirectUrl());
      body.put("ipnUrl", momoProperties.getIpnUrl());
      body.put("requestType", "captureWallet");
      body.put("extraData", extraData);
      body.put("signature", signature);
      body.put("lang", "vi");

      MomoCreateResponse response = webClient.post()
              .uri(momoProperties.getEndpoint())
              .bodyValue(body)
              .retrieve()
              .bodyToMono(MomoCreateResponse.class)
              .block();

      if (response == null || response.getPayUrl() == null) {
        throw new RuntimeException("Không lấy được link thanh toán MoMo");
      }

      log.info(
              "[SERVICE][POST][MOMO_CREATE_PAY_URL] Success orderId={}, payUrl={}",
              orderId,
              response.getPayUrl()
      );

      return response.getPayUrl();

    } catch (Exception e) {
      log.error(
              "[SERVICE][POST][MOMO_CREATE_PAY_URL] Error occurred (amount={}, orderInfo={}): {}",
              amount,
              orderInfo,
              e.getMessage(),
              e
      );
      throw e;
    }
  }
}
