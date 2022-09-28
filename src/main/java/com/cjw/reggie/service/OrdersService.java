package com.cjw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjw.reggie.entity.Orders;


public interface OrdersService extends IService<Orders> {
    void orderSubmit(Orders orders);
}
