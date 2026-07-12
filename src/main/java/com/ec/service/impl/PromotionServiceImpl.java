package com.ec.service.impl;

import com.ec.mapper.PromotionMapper;
import com.ec.pojo.Promotion;
import com.ec.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionMapper promotionMapper;

    @Override
    public List<Promotion> listAll() {
        return promotionMapper.findAll();
    }

    @Override
    public Promotion getById(Long id) {
        Promotion p = promotionMapper.findById(id);
        if (p == null) throw new IllegalArgumentException("活动不存在");
        p.setProductIds(promotionMapper.findProductIds(id));
        return p;
    }

    @Override
    @Transactional
    public Promotion create(Promotion promotion) {
        if (promotion.getEnabled() == null) promotion.setEnabled(true);
        promotionMapper.insert(promotion);
        saveProducts(promotion);
        return getById(promotion.getId());
    }

    @Override
    @Transactional
    public Promotion update(Long id, Promotion promotion) {
        promotion.setId(id);
        promotionMapper.update(promotion);
        promotionMapper.deletePromotionProducts(id);
        promotion.setProductIds(promotion.getProductIds());
        saveProducts(promotion);
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        promotionMapper.deletePromotionProducts(id);
        promotionMapper.delete(id);
    }

    private void saveProducts(Promotion promotion) {
        if (promotion.getProductIds() != null && !promotion.getProductIds().isEmpty()) {
            promotionMapper.insertPromotionProducts(promotion.getId(), promotion.getProductIds());
        }
    }
}
