package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Category;
import com.cjw.reggie.entity.Dish;
import com.cjw.reggie.service.CategoryService;
import com.cjw.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);

        return R.success("添加成功");

    }

    @GetMapping("/page")
    public R<Page> dishPage(int page, int pageSize, String name){
            Page<Dish> pageInfo = new Page<>(page,pageSize);
            Page<DishDto> pageDto =  new Page<>();

            //查询pageInfo
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,lqw);

        List<Dish> list1 = pageInfo.getRecords();
        List<DishDto> dtoList = list1.stream().map((item)->{
            DishDto dishDto = new DishDto();

            Long cateId = item.getCategoryId();
            Category category = categoryService.getById(cateId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }

            BeanUtils.copyProperties(item,dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(dtoList);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        return R.success(pageDto);

    }




}
