package com.cjw.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Dish;
import com.cjw.reggie.entity.DishFlavor;
import com.cjw.reggie.mapper.DishMapper;
import com.cjw.reggie.service.DishFlavorService;
import com.cjw.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
   private DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        List<DishFlavor> dishFlavors = dishDto.getFlavors();

        dishFlavors = dishFlavors.stream().map((flavor)->{
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);

    }
}
