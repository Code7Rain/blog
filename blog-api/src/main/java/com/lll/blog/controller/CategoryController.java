package com.lll.blog.controller;

import com.lll.blog.common.aop.Cache;
import com.lll.blog.service.CategoryService;
import com.lll.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Cache(expire = 5 * 60 * 1000,name = "categorys")
    public Result listCategory() {
        return categoryService.findAll();
    }

    @GetMapping("detail")
    @Cache(expire = 5 * 60 * 1000,name = "detail")
    public Result categoriesDetail(){
        return categoryService.findAllDetail();
    }
    @GetMapping("detail/{id}")
    @Cache(expire = 5 * 60 * 1000,name = "detailId")
    public Result categoriesDetailById(@PathVariable("id") Long id){
        return categoryService.categoriesDetailById(id);
    }
}