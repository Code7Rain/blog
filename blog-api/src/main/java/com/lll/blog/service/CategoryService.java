package com.lll.blog.service;

import com.lll.blog.vo.CategoryVo;
import com.lll.blog.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoriesDetailById(Long id);
}
