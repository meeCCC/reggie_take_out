package com.cjw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjw.reggie.common.BaseContext;
import com.cjw.reggie.common.CustomException;
import com.cjw.reggie.entity.*;
import com.cjw.reggie.mapper.OrdersMapper;
import com.cjw.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

   @Autowired
   private AddressBookService addressBookService;

   @Autowired
   private ShoppingCartService shoppingCartService;

   @Autowired
   private UserService userService;

   @Autowired
   private OrderDetailService orderDetailService;

    @Override
    public void orderSubmit(Orders orders) {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);

        List<ShoppingCart> cartList = shoppingCartService.list(lqw);

        if(cartList == null){
            throw new CustomException("购物车不能为空！");
        }

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        if(addressBook == null){
            throw new CustomException("收货地址不能为空");
        }

        Long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);


        List<OrderDetail> orderDetails = cartList.stream().map((item)->{

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orderDetailService.saveBatch(orderDetails);

        //查询用户数据
        User user = userService.getById(userId);



        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        shoppingCartService.remove(lqw);
    }


}
