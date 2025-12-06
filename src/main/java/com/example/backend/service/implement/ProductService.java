package com.example.backend.service.implement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.example.backend.entity.AuctionResult;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductImage;
import com.example.backend.entity.User;
import com.example.backend.helper.HtmlSanitizerHelper;
import com.example.backend.model.Product.CreateProductRequest;
import com.example.backend.repository.IAuctionResultRepository;
import com.example.backend.repository.ICategoryRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IUserRepository;
import com.example.backend.service.IAuctionService;
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
    private IAuctionResultRepository _auctionResultRepository;
    @Autowired
    private IAuctionService _auctionService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return _productRepository.findByIsActiveTrue(pageable);
    }

    @SuppressWarnings("null")
    @Override
    public Product getProduct(Integer productId) {
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

                    // Lọc ra sản phẩm đang hoạt động (BẮT BUỘC)
                    bool.must(f
                            .match()
                            .field("isActive")
                            .matching(true));

                    // Lọc theo keyword nếu có (BẮT BUỘC)
                    if (keyword != null && !keyword.isEmpty()) {
                        // SỬA: Sử dụng phrase matching để tránh false positive
                        // Ví dụ: "may tinh" không match với "Ngôn tình" hoặc "thời gian"
                        var keywordBool = f.bool();

                        // Ưu tiên 1: Match phrase ở productName (trọng số cao nhất = 3.0)
                        keywordBool.should(f
                                .phrase()
                                .field("productName")
                                .matching(keyword)
                                .analyzer(SearchAnalyzerConfig.VIETNAMESE_SEARCH)
                                .slop(0) // Không cho phép khoảng cách giữa các từ
                                .boost(3.0f));

                        // Ưu tiên 2: Match phrase ở description (trọng số vừa = 2.0)
                        keywordBool.should(f
                                .phrase()
                                .field("description")
                                .matching(keyword)
                                .analyzer(SearchAnalyzerConfig.VIETNAMESE_SEARCH)
                                .slop(0)
                                .boost(2.0f));

                        // Ưu tiên 3: Match phrase ở category.categoryName (trọng số thấp = 1.5)
                        keywordBool.should(f
                                .phrase()
                                .field("category.categoryName")
                                .matching(keyword)
                                .analyzer(SearchAnalyzerConfig.VIETNAMESE_SEARCH)
                                .slop(0)
                                .boost(1.5f));

                        // Yêu cầu phải match ít nhất 1 trong 3 field
                        keywordBool.minimumShouldMatchNumber(1);

                        bool.must(keywordBool);
                    }

                    // Boost sản phẩm mới hơn (đăng trong 7 ngày)
                    bool.should(f
                            .range()
                            .field("createdAt")
                            .atLeast(java.time.LocalDateTime.now().minusDays(7))
                            .boost(1.2f));

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

                    // Ưu tiên 1: Sắp xếp theo điểm số (score) giảm dần
                    // Sản phẩm mới được boost trong WHERE clause sẽ có score cao hơn
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
        newProduct.setAuxiliaryImages(productImages);

        newProduct.setProductName(request.getProductName());
        newProduct.setCurrentPrice(request.getStartPrice());

        if (request.getBuyNowPrice() != null) {
            if (request.getBuyNowPrice().compareTo(request.getStartPrice()) < 0) {
                throw new IllegalArgumentException("Buy Now Price must be greater than or equal to Start Price.");
            }
            newProduct.setBuyNowPrice(request.getBuyNowPrice());
        }

        newProduct.setStartPrice(request.getStartPrice());
        newProduct.setPriceStep(request.getPriceStep());

        String safeHtmlDescription = HtmlSanitizerHelper.sanitize(request.getDescription());
        LocalDateTime now = LocalDateTime.now();
        safeHtmlDescription = "<p>✏️ " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>"
                + safeHtmlDescription;
        newProduct.setDescription(safeHtmlDescription);

        newProduct.setEndTime(request.getEndTime());
        newProduct.setIsAutoRenew(request.getIsAutoRenew());

        return _productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public AuctionResult buyNowProduct(Integer productId, Integer buyerId) {
        Product product = _productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        if (product.getIsActive() == false) {
            throw new IllegalArgumentException("Product is not active for auction.");
        }

        if (product.getBuyNowPrice() == null) {
            throw new IllegalArgumentException("Product does not have a Buy Now option.");
        }

        // Check buyer ratings (implement in the future)
        User buyer = _userRepository.findById(buyerId).orElse(null);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found with ID: " + buyerId);
        }

        if (product.getSeller().getUserId().equals(buyerId)) {
            throw new IllegalArgumentException("Seller cannot buy their own product.");
        }

        AuctionResult existingResult = _auctionResultRepository
                .findByProductProductId(productId);
        if (existingResult != null) {
            throw new IllegalArgumentException("Product has already been sold.");
        }

        // Update product status
        product.setIsActive(false);
        product.setEndTime(LocalDateTime.now().plusSeconds(1));
        _productRepository.save(product);

        // Create auction result
        AuctionResult auctionResult = new AuctionResult();
        auctionResult.setProduct(product);
        auctionResult.setWinner(buyer);
        auctionResult.setFinalPrice(product.getBuyNowPrice());
        auctionResult.setResultTime(LocalDateTime.now());
        auctionResult.setPaymentStatus(AuctionResult.PaymentStatus.PENDING);
        AuctionResult savedAuctionResult = _auctionResultRepository.save(auctionResult);

        _auctionService.broadcastAuctionEnd(product, "Phiên đấu giá đã kết thúc vì sản phẩm đã được mua ngay.");

        return savedAuctionResult;
    }

    @Override
    public String appendDescription(Integer userId, Integer productId, String additionalDescription) {
        Product product = _productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        if (!product.getSeller().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the seller can append the product description.");
        }

        String safeAdditionalDescription = HtmlSanitizerHelper.sanitize(additionalDescription);
        LocalDateTime now = LocalDateTime.now();
        safeAdditionalDescription = "<p>✏️ " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>\n"
                + safeAdditionalDescription;
        String updatedDescription = product.getDescription() + "\n\n" + safeAdditionalDescription;
        product.setDescription(updatedDescription);
        _productRepository.save(product);

        return updatedDescription;
    }

    @Override
    public Boolean checkBiddingEligibility(Integer productId, Integer userId) {
        Product product = _productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        User user = _userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (product.getAllowUnratedBidder()) {
            return true;
        }

        Integer userRating = user.getRatingScore();
        Integer userRatingCount = user.getRatingCount();

        return userRating * 1.0 / userRatingCount >= 0.8 && userRatingCount >= 5;
    }
}
