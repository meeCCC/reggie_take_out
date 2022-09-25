package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.dto.SetmealDto;
import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.service.SetmealDishService;
import com.cjw.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

            return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> setmealPage(int page, int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        lqw.eq(Setmeal::getIsDeleted,0);
        //是否逻辑删除
        lqw.eq(Setmeal::getIsDeleted,0);

        List<Setmeal> list = setmealService.list(lqw);
        pageInfo.setRecords(list);
        return R.success(pageInfo);


    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){
        return R.success(setmealService.getWithDish(id));

    }

    @PutMapping
    public R<String> modify(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(Long ids){

        Setmeal setmeal = setmealService.getById(ids);
        if(setmeal.getStatus()==1){
            return R.error("在售商品无法删除!");
        }
        setmeal.setIsDeleted(1);
        setmealService.updateById(setmeal);

        return  R.success("删除成功");

    }

    @PostMapping({"/status/0","/status/1"})
    public R<String> statusModify(Long ids){
        Setmeal setmeal = setmealService.getById(ids);
        if(setmeal.getStatus()==1){
            setmeal.setStatus(0);
        }else {
            setmeal.setStatus(1);
        }
        setmealService.updateById(setmeal);

        return R.success("修改成功");

    }


}
