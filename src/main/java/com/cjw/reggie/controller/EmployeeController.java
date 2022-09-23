package com.cjw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjw.reggie.commen.R;
import com.cjw.reggie.entity.Employee;
import com.cjw.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {


        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库

        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        //3、如果没有查询到则返回登录失败结果

        if(emp == null){
            return R.error("用户不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("账户或者密码错误");
        }


        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("该账户已被禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
            request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工信息
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
            log.info("新增员工信息：{}"+employee.toString());

            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());

             Long empId = (Long) request.getSession().getAttribute("employee");

             employee.setCreateUser(empId);
             employee.setUpdateUser(empId);

             employeeService.save(employee);

             return R.success("添加员工成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
            log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }


    /**
     * 员工状态修改
     * @return
     */
    @PutMapping
    public R<String> status(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if (emp != null){
            return R.success(emp);
        }
        return R.error("失败了");
    }









}






























