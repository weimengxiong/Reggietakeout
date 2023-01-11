package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.DTO.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/10 10:38
 * 描述：
 */
public interface DishService extends IService<Dish> {
        public void saveWithFlavors(DishDto dishDto);

        public DishDto getByIdWithFlavor(Long id);

        //更新菜品信息同时更新口味信息
        public void updateWithFlavor(DishDto dishDto);
}
