package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 作者：Nega Nebulus
 * 时间：2023/1/10 15:55
 * 描述：文件上传下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String Basepath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需转存到指定位置，否则本次请求完成后临时文件将被删除
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取原始文件名后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成随机名称，防止重复
        String filename = UUID.randomUUID().toString() + suffix;
        //创建一个文件img
        File dir = new File(Basepath);
        //判断是否存在文件
        if(!dir.exists()){
            //目录中不存在，需要创建该文件
            dir.mkdir();
        }
        try {
            file.transferTo(new File(Basepath+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(filename);
    }

    /**
     * 文件下载功能
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(Basepath+name);
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpg");

            //读取输入流
            int lenth = 0;
            byte[] bytes = new byte[1024];
            while((lenth=fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,lenth);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }

}
