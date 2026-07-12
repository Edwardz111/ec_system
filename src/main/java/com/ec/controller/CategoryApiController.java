package com.ec.controller;

import com.ec.pojo.ProductCategory;
import com.ec.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    @Autowired
    private ProductCategoryService categoryService;

    @GetMapping("/roots")
    public List<ProductCategory> listRootCategories() {
        return categoryService.listRoots();
    }

    @GetMapping("/children")
    public List<ProductCategory> listChildren(@RequestParam("parentId") Long parentId) {
        return categoryService.listChildren(parentId);
    }
}
