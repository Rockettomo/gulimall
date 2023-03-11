package com.wcj.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 远程调用的步骤
 * 1.引入openfign
 * 2.编写接口,告诉SpringCloud这个接口需要调用远程服务
 * 3.开启远程调用功能
 */
@EnableFeignClients("com.wcj.gulimall.member.feign")
@EnableDiscoveryClient
@MapperScan("com.wcj.gulimall.member.dao")
@SpringBootApplication
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
