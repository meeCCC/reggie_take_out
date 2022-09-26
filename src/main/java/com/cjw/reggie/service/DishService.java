package com.cjw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Dish;


public interface DishService extends IService<Dish> {
    //新增带口味的对象
    public void saveWithFlavor(DishDto dishDto);

    //获取带口味的对象
    public DishDto getWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

}
