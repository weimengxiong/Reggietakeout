package com.itheima.test;

import org.junit.jupiter.api.Test;

import java.security.PrivateKey;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/10 16:35
 * 描述：
 */
public class FilenameTest {
    @Test
    public void test1(){
        String filename = "sdfdskfj.jpg";
        String s = filename.substring(filename.lastIndexOf("."));
        System.out.println(s);
    }
}
