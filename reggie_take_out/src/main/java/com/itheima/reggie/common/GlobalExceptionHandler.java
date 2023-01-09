package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/9 9:28
 * 描述：全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    // SQLIntegrityConstraintViolationException 异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionhandler(SQLIntegrityConstraintViolationException exception){
        log.info(exception.getMessage());
        //控制台返回的异常信息：Duplicate entry 'zhangsan' for key 'employee.idx_username'
        if(exception.getMessage().contains("Duplicate entry")){//当异常信息中包含有这两个信息
            String[] split = exception.getMessage().split(" ");//将该信息语句各个单词分解并组合成一个集合
            String msg = split[2]+"已存在";//username这个字段位于该集合的下标2位置处
            return R.error(msg);
        }
        return R.error("未知错误");
    }


}
