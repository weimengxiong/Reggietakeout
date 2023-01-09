package com.itheima.reggie.common;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/9 19:04
 * 描述：基于ThreadLocal的工具类，用于保存和获取用户的登录ID
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
       return threadLocal.get();
    }

}
