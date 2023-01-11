package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/9 19:36
 * 描述：
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除分类,删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        //查询当前分类是否关联菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //通过CategoryId来比对传入的ID，select * from dish where category_id = ?
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        //统计包含分类的菜品数量 大于0即为有关联菜品
        int count1= dishService.count(dishLambdaQueryWrapper);

        if(count1 > 0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("已关联菜品 无法完成删除");
        }
        //查询当前分类是否关联套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0 ){
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("已关联套餐，无法完成删除");
        }
        //正常删除
        super.removeById(ids);
    }
}
