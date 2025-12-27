package com.example.backend.service;

import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.model.SellerUpgradeRequest.ReviewSellerUpgradeRequest;

import java.util.List;
import java.util.Optional;

public interface ISellerUpgradeRequestService {

    List<SellerUpgradeRequest> getPendingRequests();

    SellerUpgradeRequest reviewRequest(
            Integer requestId,
            ReviewSellerUpgradeRequest request);

    SellerUpgradeRequest createRequest(Integer userId);

    Optional<SellerUpgradeRequest> getLatestRequestByUser(Integer userId);
}