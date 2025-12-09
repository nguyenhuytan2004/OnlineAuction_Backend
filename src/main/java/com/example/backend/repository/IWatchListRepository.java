package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.WatchList;

public interface IWatchListRepository extends JpaRepository<WatchList, Integer> {
    List<WatchList> findByUserUserId(Integer userId);

    WatchList findByUserUserIdAndProductProductId(Integer userId, Integer productId);
}
