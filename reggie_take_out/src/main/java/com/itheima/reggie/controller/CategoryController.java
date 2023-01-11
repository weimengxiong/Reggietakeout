package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/9 19:45
 * 描述：
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
   public R<String> save(@RequestBody Category category){
        log.info("category{}",category);
        //调用MP中的save方法
        categoryService.save(category);
        return R.success("新增分类成功");
   }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
   public R<Page> page(int page,int pageSize){
        log.info("添加菜品，page为{},pageSize为{}",page,pageSize);
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //设置升序排序 传入菜品分类中的排序sort
        queryWrapper.orderByAsc(Category::getSort);
        //调用MP中的page方法 传入pageInfo和queryWrapper
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
   }

    /**
     * 删除菜品分类
     * @param ids
     * @return
     */
    @DeleteMapping
   public R<String> delete(Long ids){
        log.info("删除分类，id为{}",ids);


//      categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("菜品删除成功");
   }

    /**
     * 修改菜品分类
     * @param category
     * @return
     */
    @PutMapping
   public R<String> update(@RequestBody Category category){
        log.info("修改分类category{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
   }
    @GetMapping("/list")
   public R<List<Category>> list(Category category){
        //创建构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //设置条件
        queryWrapper.eq(category.getType() != 0,Category::getType,category.getType());
        //设置排序 根据sort升序排列，根据更新时间降序排列
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //调用MP的list方法
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
   }

}
