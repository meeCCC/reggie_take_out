package com.cjw.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.common.BaseContext;
import com.cjw.reggie.common.CustomException;
import com.cjw.reggie.common.R;
import com.cjw.reggie.dto.OrdersDto;
import com.cjw.reggie.entity.AddressBook;
import com.cjw.reggie.entity.OrderDetail;
import com.cjw.reggie.entity.Orders;
import com.cjw.reggie.entity.ShoppingCart;
import com.cjw.reggie.service.AddressBookService;
import com.cjw.reggie.service.OrderDetailService;
import com.cjw.reggie.service.OrdersService;
import com.cjw.reggie.service.ShoppingCartService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Transactional
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders){


        ordersService.orderSubmit(orders);

        return R.success("订单提交成功");
    }

    @GetMapping("/userPage")
    public R<Page> getOrders(int page,int pageSize){
        Page pageInfo = new Page(page,pageSize);

        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId,userId);
        List<Orders> list = ordersService.list(lqw);

        pageInfo.setRecords(list);
        return R.success(pageInfo);


    }

    @GetMapping("/page")
    public R<Page> getOrdersInBackend(int page, int pageSize, Long number){

        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(number!=null,Orders::getNumber,number);

        List<Orders> list = ordersService.list(lqw);


        pageInfo.setRecords(list);
        return R.success(pageInfo);


    }

    /**
     * 派送，修改状态
     * @return
     */
    @PutMapping
    public R<String> takeOrder(@RequestBody Orders orders){

        Orders order = ordersService.getById(orders.getId());
        order.setStatus(orders.getStatus());
        ordersService.updateById(order);

        return R.success("修改成功，正在派送....");


    }

    /*@PostMapping("/again")
    public R<OrdersDto> againOrder(@RequestBody Orders orders){

        Long orderId = orders.getId();

        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();

        lqw.eq(orderId!=null,OrderDetail::getOrderId,orderId);

        //查询出的订单菜品
        List<OrderDetail> orderDetailsListAgain = orderDetailService.list(lqw);

        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setOrderDetails(orderDetailsListAgain);

        Orders orderAgain = ordersService.getById(orderId);

        BeanUtils.copyProperties(orderAgain,ordersDto);

        return R.success(ordersDto);

    }*/

    @PostMapping("/again")
    public R<List<ShoppingCart>> againOrder(@RequestBody Orders orders){

        Long orderId = orders.getId();

        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();

        lqw.eq(orderId!=null,OrderDetail::getOrderId,orderId);

        //查询出的订单菜品
        List<OrderDetail> orderDetailsListAgain = orderDetailService.list(lqw);

        List<ShoppingCart> shoppingCartList = orderDetailsListAgain.stream().map((item)->{

            ShoppingCart shoppingCart = new ShoppingCart();

            BeanUtils.copyProperties(item,shoppingCart);

            shoppingCart.setUserId(BaseContext.getCurrentId());

            return shoppingCart;

        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCartList);



        return R.success(shoppingCartList);

    }


}
