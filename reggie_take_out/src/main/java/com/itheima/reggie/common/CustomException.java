package com.itheima.reggie.common;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/10 11:49
 * 描述：自定义异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
