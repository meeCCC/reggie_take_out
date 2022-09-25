package com.cjw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjw.reggie.dto.SetmealDto;
import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.entity.SetmealDish;
import com.cjw.reggie.mapper.SetmealMapper;
import com.cjw.reggie.service.SetmealDishService;
import com.cjw.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //为套餐菜品表设置套餐id
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //调用数据层，保存套餐菜品数据
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public SetmealDto getWithDish(Long id) {

        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(id!=null,SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishList = setmealDishService.list(lqw);

        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;

    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();

        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(lqw);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
