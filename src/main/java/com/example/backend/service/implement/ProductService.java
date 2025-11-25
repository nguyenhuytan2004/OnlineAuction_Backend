package com.example.backend.service.implement;

import java.util.List;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.config.SearchAnalyzerConfig;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductImage;
import com.example.backend.helper.HtmlSanitizerHelper;
import com.example.backend.model.Product.CreateProductRequest;
import com.example.backend.repository.ICategoryRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IProductService;

import jakarta.persistence.EntityManager;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository _productRepository;
    @Autowired
    private IUserRepository _userRepository;
    @Autowired
    private ICategoryRepository _categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Product> getAllProducts() {
        return _productRepository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    public Product getProductById(Integer productId) {
        return _productRepository.findById(productId).orElse(null);
    }

    @Override
    public Page<Product> getProductsByCategoryId(Integer categoryId, Pageable pageable) {
        return _productRepository.findByCategoryCategoryId(categoryId, pageable);
    }

    @Override
    public List<Product> getTop5EndingSoonProducts() {
        return _productRepository.findTop5ByOrderByEndTimeAsc();
    }

    @Override
    public List<Product> getTop5MostAuctionedProducts() {
        return _productRepository.findTop5ByOrderByBidCountDesc();
    }

    @Override
    public List<Product> getTop5HighestPricedProducts() {
        return _productRepository.findTop5ByOrderByCurrentPriceDesc();
    }

    @Override
    public List<Product> getTop5RelatedProducts(Integer categoryId, Integer productId) {
        return _productRepository.findTop5ByCategoryCategoryIdAndProductIdNotOrderByEndTimeAsc(categoryId, productId);
    }

    @SuppressWarnings("null")
    @Override
    public Page<Product> searchProducts(String keyword, Integer categoryId, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager.unwrap(org.hibernate.Session.class));
        SearchResult<Product> result = searchSession.search(Product.class)
                .where(f -> {
                    var bool = f.bool();

                    // Lọc ra sản phẩm đang hoạt động
                    bool.must(f
                            .match()
                            .field("isActive")
                            .matching(true));

                    // Lọc theo keyword nếu có
                    if (keyword != null && !keyword.isEmpty()) {
                        bool.must(f
                                .match()
                                .fields("productName", "description", "category.categoryName")
                                .matching(keyword)
                                .fuzzy(1) // Cho phép lỗi chính tả nhỏ
                                .analyzer(SearchAnalyzerConfig.VIETNAMESE_SEARCH));
                    }

                    // Lọc theo categoryId nếu có
                    if (categoryId != null) {
                        bool.must(f
                                .match()
                                .field("category.categoryId")
                                .matching(categoryId));
                    }

                    return bool;
                })
                .sort(f -> {
                    var sortStep = f.composite();

                    // Ưu tiên 1 (luôn luôn): Sắp xếp điểm phù hợp giảm dần
                    sortStep.add(f.score().desc());

                    // Ưu tiên 2: Sắp xếp theo các trường được chỉ định trong pageable
                    if (pageable.getSort().isSorted()) {
                        for (var order : pageable.getSort()) {
                            if (order.isAscending()) {
                                sortStep.add(f.field(order.getProperty()).asc());
                            } else {
                                sortStep.add(f.field(order.getProperty()).desc());
                            }
                        }
                        return sortStep;
                    }

                    // Ưu tiên 2(Mặc định): Sắp xếp theo endTime tăng dần nếu không có sắp xếp nào
                    // được chỉ định
                    return sortStep.add(f.field("endTime").asc());

                })
                .fetch(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());

        return result.hits().isEmpty() ? Page.empty()
                : new PageImpl<>(
                        result.hits(),
                        pageable,
                        result.total().hitCount());
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request, Integer sellerId) {
        Product newProduct = new Product();

        newProduct.setSeller(_userRepository.findById(sellerId).orElse(null));

        newProduct.setCategory(_categoryRepository.findById(request.getCategoryId()).orElse(null));

        newProduct.setMainImageUrl(request.getMainImageUrl());

        List<ProductImage> productImages = request.getAuxiliaryImageUrls().stream().map(url -> {
            ProductImage img = new ProductImage();
            img.setProduct(newProduct);
            img.setImageUrl(url);
            return img;
        }).toList();
        newProduct.setAuxiliaryImageUrls(productImages);

        newProduct.setProductName(request.getProductName());
        newProduct.setCurrentPrice(request.getStartPrice());
        newProduct.setBuyNowPrice(request.getBuyNowPrice());
        newProduct.setStartPrice(request.getStartPrice());
        newProduct.setPriceStep(request.getPriceStep());

        String safeHtmlDescription = HtmlSanitizerHelper.sanitize(request.getDescription());
        newProduct.setDescription(safeHtmlDescription);

        newProduct.setEndTime(request.getEndTime());
        newProduct.setIsAutoRenew(request.getIsAutoRenew());

        return _productRepository.save(newProduct);
    }
}
