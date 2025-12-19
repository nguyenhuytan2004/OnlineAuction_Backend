package com.example.backend.service;

import com.example.backend.entity.SellerUpgradeRequest;
import com.example.backend.model.SellerUpgradeRequest.ReviewSellerUpgradeRequest;

import java.util.List;

public interface ISellerUpgradeRequestService {

    List<SellerUpgradeRequest> getPendingRequests();

    SellerUpgradeRequest reviewRequest(
            Integer requestId,
            ReviewSellerUpgradeRequest request);
}