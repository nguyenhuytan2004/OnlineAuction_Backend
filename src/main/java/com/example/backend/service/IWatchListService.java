package com.example.backend.service;

import java.util.List;

import com.example.backend.entity.WatchList;

public interface IWatchListService {
    public List<WatchList> getWatchList(Integer userId);

    public WatchList addToWatchList(Integer userId, Integer productId);

    public WatchList removeFromWatchList(Integer userId, Integer productId);

    public boolean isInWatchList(Integer userId, Integer productId);
}
