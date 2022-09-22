package com.cjw.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjw.reggie.entity.Employee;
import com.cjw.reggie.mapper.EmployeeMapper;
import com.cjw.reggie.service.EmployeeService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
