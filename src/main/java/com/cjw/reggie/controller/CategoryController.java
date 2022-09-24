package com.cjw.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.entity.Category;
import com.cjw.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;



    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
            log.info("category : "+ category);
            categoryService.save(category);
            return R.success("成功添加");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        lqw.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);

    }

    /**
     * 删除套餐分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){

        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> modifyCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        lqw.eq(category != null,Category::getType,category.getType());
        lqw.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List list = categoryService.list(lqw);
        return R.success(list);

    }

















}
