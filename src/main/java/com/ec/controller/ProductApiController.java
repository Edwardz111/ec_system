package com.ec.controller;

import com.ec.pojo.Product;
import com.ec.pojo.vo.PageResult;
import com.ec.service.CatalogProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品 REST API（原 product 模块，已统一为 MyBatis + com.ec 包）
 */
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private CatalogProductService catalogProductService;

    @GetMapping
    public ResponseEntity<List<Product>> listAll() {
        return ResponseEntity.ok(catalogProductService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogProductService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogProductService.create(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(catalogProductService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResult<Product>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(catalogProductService.searchProducts(
                keyword, categoryId, minPrice, maxPrice, sortBy, page, size));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<Product>> recommend(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(catalogProductService.getRecommendedProducts(categoryId, limit));
    }

    @GetMapping("/{id}/reviews-temp")
    public ResponseEntity<List<Map<String, Object>>> listReviewsTemp(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(catalogProductService.listReviews(id, limit));
    }

    @GetMapping("/{id}/reviews-summary-temp")
    public ResponseEntity<Map<String, Object>> reviewSummaryTemp(@PathVariable Long id) {
        return ResponseEntity.ok(catalogProductService.reviewSummary(id));
    }
}
