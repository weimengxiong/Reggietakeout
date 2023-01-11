package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.DTO.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/10 10:39
 * 描述：
 */
@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
        @Autowired
        private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存不同的口味选择信息
     * @param dishDto
     */

    @Override
    public void saveWithFlavors(DishDto dishDto) {
        //新增的菜品基本信息保存到菜品表dish中
        this.save(dishDto);

        Long dishId = dishDto.getId();
        //菜品口味调取
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将菜品口味ID与菜品ID调整一致，确保数据统一
        flavors.stream().map((item) -> {   //将集合转变成流，使用map对集合中的元素进行修改
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());//将修改后的流重新转变成集合
        //保存菜品口味信息到菜品口味表dish_flavor中
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //按照ID查询菜品信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //复制内容
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前ID的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品信息查询口味信息
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //列出查询出的内容
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //拷贝得到的dishDto设置口味信息
        dishDto.setFlavors(flavors);
        return dishDto;

    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品信息
        this.updateById(dishDto);
        //更新口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        //先删除原先的数据
        dishFlavorService.remove(queryWrapper);
        //将本次提交的数据插入
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {   //将集合转变成流，使用map对集合中的元素进行修改
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());//将修改后的流重新转变成集合
        dishFlavorService.saveBatch(flavors);
    }
}
