package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Category;
import com.cjw.reggie.entity.Dish;
import com.cjw.reggie.entity.DishFlavor;
import com.cjw.reggie.service.CategoryService;
import com.cjw.reggie.service.DishFlavorService;
import com.cjw.reggie.service.DishService;
import com.cjw.reggie.service.impl.DishServiceImpl;
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

    @Autowired
    private DishFlavorService dishFlavorService;

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
        lqw.eq(Dish::getIsDeleted,0);
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

    @GetMapping("/{id}")
    public R<DishDto> getToModify(@PathVariable Long id){

        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }


    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("修改成功");

    }

    @DeleteMapping
    public R<String> deleteDishes( Long ids){
        log.info("id= {}"+ids);
        Dish dish = dishService.getById(ids);
        if(dish.getStatus()==1){
            return R.error("在售菜品无法删除");
        }

        dish.setIsDeleted(1);
        dishService.updateById(dish);

        return R.success("删除成功");

    }

    @PostMapping({"/status/0","/status/1"})
    public R<String> stopSale( Long ids){
        log.info("id = {}",ids);

        Dish dish = dishService.getById(ids);

        Integer status = dish.getStatus();

        if(status == 1){
            dish.setStatus(0);
        }else {
            dish.setStatus(1);
        }

        dishService.updateById(dish);


        return R.success("修改完成");
    }

    @GetMapping("/list")
    public R<List<Dish>> dishList(Dish dish){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes = dishService.list(lqw);

        return R.success(dishes);
    }


}
