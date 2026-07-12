package com.ec.controller;

import com.ec.pojo.Product;
import com.ec.pojo.ProductCategory;
import com.ec.pojo.Promotion;
import com.ec.pojo.vo.PageResult;
import com.ec.service.CatalogProductService;
import com.ec.service.ProductCategoryService;
import com.ec.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

    @Autowired
    private CatalogProductService catalogProductService;
    @Autowired
    private ProductCategoryService categoryService;
    @Autowired
    private PromotionService promotionService;

    @GetMapping("/categories")
    public List<ProductCategory> listAllCategories() {
        return categoryService.listAll();
    }

    @GetMapping("/categories/leaves")
    public List<ProductCategory> listLeafCategories() {
        return categoryService.listLeafCategories();
    }

    @PostMapping("/categories")
    public ProductCategory createCategory(@RequestBody CategoryRequest req) {
        return categoryService.create(req.name, req.parentId);
    }

    @PutMapping("/categories/{id}")
    public ProductCategory updateCategory(@PathVariable Long id, @RequestBody CategoryRequest req) {
        return categoryService.update(id, req.name, req.parentId);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @GetMapping("/products")
    public PageResult<Product> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return catalogProductService.searchProducts(null, null, null, null, "newest", page, size);
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody ProductRequest req) {
        ProductCategory category = categoryService.requireLeafCategory(req.categoryId);
        Product p = new Product();
        p.setName(req.name);
        p.setDescription(req.description);
        p.setPrice(req.price);
        p.setStock(req.stock);
        p.setCategoryId(category.getCategoryId());
        p.setEnabled(req.enabled != null ? req.enabled : Boolean.TRUE);
        p.setSalesVolume(0);
        return catalogProductService.create(p);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
        ProductCategory category = categoryService.requireLeafCategory(req.categoryId);
        Product p = catalogProductService.getById(id);
        p.setName(req.name);
        p.setDescription(req.description);
        p.setPrice(req.price);
        p.setStock(req.stock);
        p.setCategoryId(category.getCategoryId());
        if (req.enabled != null) p.setEnabled(req.enabled);
        return catalogProductService.update(id, p);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        catalogProductService.delete(id);
    }

    @GetMapping("/promotions")
    public List<Promotion> listPromotions() {
        return promotionService.listAll();
    }

    @PostMapping("/promotions")
    public Promotion createPromotion(@RequestBody PromotionRequest req) {
        Promotion p = new Promotion();
        p.setName(req.name);
        p.setType(req.type);
        p.setDescription(req.description);
        p.setRule(req.rule);
        p.setEnabled(req.enabled != null ? req.enabled : Boolean.TRUE);
        p.setProductIds(req.productIds);
        return promotionService.create(p);
    }

    @PutMapping("/promotions/{id}")
    public Promotion updatePromotion(@PathVariable Long id, @RequestBody PromotionRequest req) {
        Promotion p = new Promotion();
        p.setName(req.name);
        p.setType(req.type);
        p.setDescription(req.description);
        p.setRule(req.rule);
        p.setEnabled(req.enabled);
        p.setProductIds(req.productIds);
        return promotionService.update(id, p);
    }

    @DeleteMapping("/promotions/{id}")
    public void deletePromotion(@PathVariable Long id) {
        promotionService.delete(id);
    }

    public static class CategoryRequest {
        public String name;
        public Long parentId;
    }

    public static class ProductRequest {
        public String name;
        public String description;
        public BigDecimal price;
        public Integer stock;
        public Long categoryId;
        public Boolean enabled;
    }

    public static class PromotionRequest {
        public String name;
        public String type;
        public String description;
        public String rule;
        public Boolean enabled;
        public List<Long> productIds;
    }
}
