package com.cjw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjw.reggie.dto.SetmealDto;
import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.entity.SetmealDish;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
