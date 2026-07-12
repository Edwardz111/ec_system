package com.ec.service;

import com.ec.pojo.Promotion;

import java.util.List;

public interface PromotionService {
    List<Promotion> listAll();
    Promotion getById(Long id);
    Promotion create(Promotion promotion);
    Promotion update(Long id, Promotion promotion);
    void delete(Long id);
}
