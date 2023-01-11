package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/6 11:27
 * 描述：
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1 根据页面提交的密码进行加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//创建查询对象
        queryWrapper.eq(Employee::getUsername,employee.getUsername()); //比对页面提交的用户名与数据库的用户名
        Employee emp = employeeService.getOne(queryWrapper);//通过getOne方法调出加有唯一约束的数据库数据

        //没有查询到返回失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4 比对密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5 判断员工登录状态是否禁用，如为禁用则返回登录失败
        if(emp.getStatus() == 0 ){
            return R.error("登录失败，账号已禁用");
        }

        //6 登录成功，将员工ID存入session并返回登录结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
     public R<String> logOut(HttpServletRequest request){
        //清理session中保存的当前员工ID
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息，员工信息为{}",employee.toString());
        //前端传入的信息有密码，创建时间、更新时间、创建人、更新人等信息未填入
        //设置初始密码123456，使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间
//        employee.setCreateTime(LocalDateTime.now());
//
//        //设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //设置创建人,通过request获得当前员工对象的ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);

        return R.success("新增员工成功");

    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //泛型使用MybatisPlus封装的Page类
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageinfo = new Page(page,pageSize);

        //构造调教构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //设置过滤条件
        if(name != null){
            queryWrapper.like(Employee::getName,name);
        }
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 根据员工Id修改信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //判断employee中的属性是否完成封装
        log.info(employee.toString());
        //获取员工Id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //Long型的员工ID存在精度问题，后三位被自动四舍五入导致无法与数据库相关ID匹配，通过添加消息转换器，调用该转换器
        //来做到将Long型的ID数据转换成String型,确保精度
        //更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置更新人
        employee.setUpdateUser(empId);

        //调用MP中的根据Id更新数据的方法
        employeeService.updateById(employee);
        return  R.success("员工信息修改成功");
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息");
        //通过分析，前端所需要的对象为id对象，此处获取到员工ID即可调用员工信息
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("未查询到员工信息");
    }
}
