package com.lll.blog.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Q on 2022/4/18.
 */
@SpringBootApplication
@ComponentScan({"com.lll.blog.admin.mapper"})
public class AdminApp {
    public static void main(String[] args) {
        SpringApplication.run(AdminApp.class,args);
    }
}
