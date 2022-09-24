package com.cjw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjw.reggie.commen.CustomException;
import com.cjw.reggie.entity.Category;
import com.cjw.reggie.entity.Dish;
import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.mapper.CategoryMapper;
import com.cjw.reggie.service.CategoryService;
import com.cjw.reggie.service.DishService;
import com.cjw.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLqw = new LambdaQueryWrapper<>();
        dishLqw.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLqw);

        if (count1 >0){
            //抛出异常
            throw new CustomException("该分类下关联有菜品，删除失败");
        }

        LambdaQueryWrapper<Setmeal> setmealLqw = new LambdaQueryWrapper<>();
        setmealLqw.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLqw);

        if (count2 > 0){
            //抛出异常
            throw new CustomException("该分类下关联有套餐，删除失败");
        }

        super.removeById(id);



    }
}
