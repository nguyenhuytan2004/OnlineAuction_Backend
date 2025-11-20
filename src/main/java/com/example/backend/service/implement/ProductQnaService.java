package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.ProductQnA.ProductQuestion;
import com.example.backend.repository.IProductQnaRepository;
import com.example.backend.service.IProductQnaService;

@Service
public class ProductQnaService implements IProductQnaService {
    @Autowired
    private IProductQnaRepository _productQnaRepository;

    @Override
    public List<ProductQuestion> getProductQnaByProductId(Integer productId) {
        return _productQnaRepository.findByProductProductId(productId);
    }
}
