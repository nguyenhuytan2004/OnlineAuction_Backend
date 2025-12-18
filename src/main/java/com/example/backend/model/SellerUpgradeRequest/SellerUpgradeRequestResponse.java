package com.example.backend.model.SellerUpgradeRequest;

import com.example.backend.entity.SellerUpgradeRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerUpgradeRequestResponse {

    private Integer requestId;
    private Integer userId;
    private String userEmail;
    private String userFullName;
    private SellerUpgradeRequest.Status status;
    private LocalDateTime requestAt;
    private LocalDateTime reviewedAt;
    private String comments;

    public SellerUpgradeRequestResponse(SellerUpgradeRequest request) {
        this.requestId = request.getRequestId();
        this.userId = request.getUser().getUserId();
        this.userEmail = request.getUser().getEmail();
        this.userFullName = request.getUser().getFullName();
        this.status = request.getStatus();
        this.requestAt = request.getRequestAt();
        this.reviewedAt = request.getReviewedAt();
        this.comments = request.getComments();
    }
}
