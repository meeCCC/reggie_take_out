package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cjw.reggie.common.BaseContext;
import com.cjw.reggie.common.R;
import com.cjw.reggie.entity.ShoppingCart;
import com.cjw.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> saveShoppingCart(@RequestBody ShoppingCart shoppingCart){

        Long userId = BaseContext.getCurrentId();

        shoppingCart.setUserId(userId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,userId);



        if (dishId != null) {
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else{
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart getOneInCart = shoppingCartService.getOne(lqw);

        if (getOneInCart!=null) {
            Integer number = getOneInCart.getNumber();
            getOneInCart.setNumber(number+1);
            shoppingCartService.updateById(getOneInCart);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            getOneInCart = shoppingCart;
        }

        return R.success(getOneInCart);

    }

    @PostMapping("/sub")
    public R<ShoppingCart> subInShoppingCart(@RequestBody  ShoppingCart shoppingCart){

        Long userId = BaseContext.getCurrentId();

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,userId);

        if(dishId!=null){
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart getOne = shoppingCartService.getOne(lqw);

        if (getOne==null) {
            return R.error("删除失败！请合理操作！");
        }else {
            Integer number = getOne.getNumber();
            getOne.setNumber(number-1);
            if(getOne.getNumber()==0){
                shoppingCartService.removeById(getOne.getId());
            }
            shoppingCartService.updateById(getOne);
        }

            return R.success(getOne);


    }

    /**
     * 获取购物车菜品
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getCartList(){
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,userId);

        List<ShoppingCart> cartList = shoppingCartService.list(lqw);

        return R.success(cartList);
    }

    @DeleteMapping("/clean")
    public R<String> cleanCart(){
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,userId);

        shoppingCartService.remove(lqw);
        return R.success("清除成功");

    }


}
