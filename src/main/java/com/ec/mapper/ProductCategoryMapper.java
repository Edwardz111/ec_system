package com.ec.mapper;

import com.ec.pojo.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {

    List<ProductCategory> findAll();

    List<ProductCategory> findRoots();

    List<ProductCategory> findByParentId(Long parentId);

    ProductCategory findById(Long categoryId);

    int insert(ProductCategory category);

    int update(ProductCategory category);

    int delete(Long categoryId);

    int countChildren(Long categoryId);
}
