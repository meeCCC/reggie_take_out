package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.common.R;
import com.cjw.reggie.dto.DishDto;
import com.cjw.reggie.entity.Category;
import com.cjw.reggie.entity.Dish;
import com.cjw.reggie.entity.DishFlavor;
import com.cjw.reggie.service.CategoryService;
import com.cjw.reggie.service.DishFlavorService;
import com.cjw.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    public R<String> deleteDish(String ids){
        log.info("id= {}"+ids);

        //多个菜品的id
        String[] idses = ids.split(",");
        for (String id: idses) {

            Dish dish = dishService.getById(id);
            if(dish.getStatus()==1){
                return R.error("在售菜品无法删除");
            }

            dish.setIsDeleted(1);
            dishService.updateById(dish);

        }



        return R.success("删除成功");

    }


    /**
     * 停售，批量停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stopSale( String ids){
        log.info("id = {}",ids);

        String[] idses = ids.split(",");

        for (String id : idses) {
            Dish dish = dishService.getById(id);

            dish.setStatus(0);

            dishService.updateById(dish);
        }

        return R.success("修改完成");
    }

    /**
     * 起售，批量起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> beginSale( String ids){
        log.info("id = {}",ids);

        String[] idses = ids.split(",");

        for (String id : idses) {
            Dish dish = dishService.getById(id);

            dish.setStatus(1);

            dishService.updateById(dish);
        }

        return R.success("修改完成");
    }

    /*@GetMapping("/list")
    public R<List<Dish>> dishList(Dish dish){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes = dishService.list(lqw);

        return R.success(dishes);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> dishList(Dish dish){

        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId,dish.getCategoryId());
        lqw1.eq(Dish::getStatus,1);

        List<Dish> dishList = dishService.list(lqw1);
        List<DishDto> dishDtoList = null;



        dishDtoList = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            BeanUtils.copyProperties(item,dishDto);

            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(lqw);

            dishDto.setFlavors(flavors);

            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);





    }


}
