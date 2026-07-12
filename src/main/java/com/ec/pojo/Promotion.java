package com.ec.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Promotion implements Serializable {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String rule;
    private Boolean enabled;
    private List<Long> productIds = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRule() { return rule; }
    public void setRule(String rule) { this.rule = rule; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}
