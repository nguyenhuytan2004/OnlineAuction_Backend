package com.example.backend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "auction.renew")
@Data
public class AuctionProperties {

    // Nếu có người đặt giá trong khoảng thời gian này trước khi phiên đấu giá kết
    // thúc thì sẽ kích hoạt gia hạn
    // endTime - currentTime <= triggerDuration
    private Duration triggerDuration = Duration.ofMinutes(5);

    // Thời gian gia hạn thêm cho phiên đấu giá khi có người đặt giá trong khoảng
    // thời gian kích hoạt gia hạn
    // endTime = endTime + extendDuration
    private Duration extendDuration = Duration.ofMinutes(10);
}
