package com.cjw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);
}
