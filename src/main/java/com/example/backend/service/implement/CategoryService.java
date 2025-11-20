package com.example.backend.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Category;
import com.example.backend.repository.ICategoryRepository;
import com.example.backend.service.ICategoryService;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private ICategoryRepository _categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return _categoryRepository.findAll();
    }
}
