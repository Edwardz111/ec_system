package com.ec.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体 —— 对应 products 表（整合 ec 订单模块 + product 商品管理模块）
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private Integer stock;
    private Integer salesVolume;
    private Long categoryId;
    private String categoryName;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSalesVolume() { return salesVolume; }
    public void setSalesVolume(Integer salesVolume) { this.salesVolume = salesVolume; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    /** 兼容 product 模块 JSON 中的 category 嵌套对象 */
    public ProductCategory getCategory() {
        if (categoryId == null && categoryName == null) return null;
        ProductCategory c = new ProductCategory();
        c.setCategoryId(categoryId);
        c.setName(categoryName);
        return c;
    }

    public void setCategory(ProductCategory category) {
        if (category != null) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getName();
        }
    }
}
