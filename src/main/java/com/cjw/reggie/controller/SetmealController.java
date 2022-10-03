package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.common.R;
import com.cjw.reggie.dto.SetmealDto;
import com.cjw.reggie.entity.Setmeal;
import com.cjw.reggie.service.CategoryService;
import com.cjw.reggie.service.SetmealDishService;
import com.cjw.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CategoryService categoryService;

    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

            return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> setmealPage(int page, int pageSize,String name){
        Page<SetmealDto> pageInfo = new Page<>(page,pageSize);


        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        //是否逻辑删除
        lqw.eq(Setmeal::getIsDeleted,0);

        List<Setmeal> list = setmealService.list(lqw);

        List<SetmealDto> setmealDtoList = list.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            String name1 = categoryService.getById(item.getCategoryId()).getName();

            setmealDto.setCategoryName(name1);

            return setmealDto;

        }).collect(Collectors.toList());


        pageInfo.setRecords(setmealDtoList);
        return R.success(pageInfo);


    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){
        return R.success(setmealService.getWithDish(id));

    }

    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> modify(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(String ids){

        String[] idses = ids.split(",");

        for (String id : idses) {
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal.getStatus()==1){
                return R.error("在售商品无法删除!");
            }
            setmeal.setIsDeleted(1);
            setmealService.updateById(setmeal);
        }

        return  R.success("删除成功");

    }

    /**
     * 停售，批量停售
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping("/status/0")
    public R<String> stopSale(String ids){

        String[] idses = ids.split(",");

        for (String id : idses) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }

        return R.success("修改成功");

    }

    /**
     * 起售，批量起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> beginSale(String ids){

        String[] idses = ids.split(",");

        for (String id : idses) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }

        return R.success("修改成功");

    }


    /**
     * 获取套餐列表
     * @param categoryId
     * @param status
     * @return
     */
    @Cacheable(value = "setmealCache",key = "'setmeal_'+#categoryId+#status")
    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealList(Long categoryId,int status){

        List<Setmeal> setmealList = null;

        /*String setmealId = "setmeal_" + categoryId + "_1";

        setmealList = (List<Setmeal>) redisTemplate.opsForValue().get(setmealId);*/

        if (setmealList!=null){
            return R.success(setmealList);
        }

        LambdaQueryWrapper<Setmeal> lqw =new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId,categoryId);
        lqw.eq(Setmeal::getStatus,status);

         setmealList = setmealService.list(lqw);


         //redisTemplate.opsForValue().set(setmealId,setmealList,60, TimeUnit.MINUTES);

        return R.success(setmealList);


    }


}
