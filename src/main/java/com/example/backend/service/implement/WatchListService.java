package com.example.backend.service.implement;

import java.util.List;

import com.example.backend.model.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.entity.WatchList;
import com.example.backend.repository.IWatchListRepository;
import com.example.backend.service.IProductService;
import com.example.backend.service.IUserService;
import com.example.backend.service.IWatchListService;

@Service
public class WatchListService implements IWatchListService {

    @Autowired
    private IWatchListRepository _watchListRepository;
    @Autowired
    private IUserService _userService;
    @Autowired
    private IProductService _productService;

    @Override
    public List<WatchList> getWatchList(Integer userId) {
        return _watchListRepository.findByUserUserId(userId);
    }

    @Override
    public WatchList addToWatchList(Integer userId, Integer productId) {
        User user = _userService.getUser(userId);
        Product product = _productService.getProductById(productId);

        WatchList watchList = new WatchList();
        watchList.setUser(user);
        watchList.setProduct(product);

        return _watchListRepository.save(watchList);
    }
}
