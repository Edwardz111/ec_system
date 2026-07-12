package com.ec.service.impl;

import com.ec.mapper.ProductMapper;
import com.ec.pojo.Product;
import com.ec.pojo.vo.PageResult;
import com.ec.service.CatalogProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CatalogProductServiceImpl implements CatalogProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> listAll() {
        return productMapper.findAll();
    }

    @Override
    public Product getById(Long id) {
        Product p = productMapper.findById(id);
        if (p == null) throw new IllegalArgumentException("商品不存在，id=" + id);
        return p;
    }

    @Override
    public Product create(Product product) {
        product.setId(null);
        if (product.getSalesVolume() == null) product.setSalesVolume(0);
        if (product.getEnabled() == null) product.setEnabled(true);
        productMapper.insert(product);
        return productMapper.findById(product.getId());
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = getById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        if (product.getCategoryId() != null) existing.setCategoryId(product.getCategoryId());
        if (product.getEnabled() != null) existing.setEnabled(product.getEnabled());
        productMapper.update(existing);
        return productMapper.findById(id);
    }

    @Override
    public void delete(Long id) {
        productMapper.delete(id);
    }

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", normalizeKeyword(keyword));
        params.put("categoryId", categoryId);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("sortBy", sortBy == null ? "newest" : sortBy);
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        params.put("offset", safePage * safeSize);
        params.put("limit", safeSize);
        long total = productMapper.countSearch(params);
        List<Product> content = productMapper.search(params);
        return new PageResult<>(content, safePage, safeSize, total);
    }

    @Override
    public List<Product> getRecommendedProducts(Long categoryId, int limit) {
        int size = limit <= 0 ? 10 : limit;
        List<Product> candidates = categoryId != null
                ? productMapper.findByCategoryId(categoryId)
                : productMapper.findAll();
        if (candidates.size() <= size) return candidates;
        List<Product> copy = new ArrayList<>(candidates);
        Collections.shuffle(copy);
        return copy.subList(0, size);
    }

    @Override
    public List<Map<String, Object>> listReviews(Long productId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return productMapper.listReviews(productId, safeLimit);
    }

    @Override
    public Map<String, Object> reviewSummary(Long productId) {
        Map<String, Object> m = productMapper.reviewSummary(productId);
        if (m == null) {
            return Map.of("avgRating", 5, "reviewCount", 0);
        }
        long count = m.get("reviewCount") != null ? ((Number) m.get("reviewCount")).longValue() : 0;
        double avg = m.get("avgRating") != null ? ((Number) m.get("avgRating")).doubleValue() : 0;
        if (count == 0 || avg <= 0) {
            java.util.HashMap<String, Object> def = new java.util.HashMap<>(m);
            def.put("avgRating", 5);
            def.put("reviewCount", 0L);
            return def;
        }
        return m;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String kw = keyword.trim();
        String lower = kw.toLowerCase(Locale.ROOT);
        if (lower.contains("xiaomi")) return "小米";
        if (lower.contains("huawei")) return "华为";
        if (lower.contains("lianyiqun")) return "连衣裙";
        return kw;
    }
}
