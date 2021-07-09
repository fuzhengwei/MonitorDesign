package cn.bugstack.middleware.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 *
 * ASM：-javaagent:E:\itstack\git\github.com\MonitorDesign\cn-bugstack-middleware-asm\target\cn-bugstack-middleware-asm.jar
 * Bytebuddy：-javaagent:E:\itstack\git\github.com\MonitorDesign\cn-bugstack-middleware-bytebuddy\target\cn-bugstack-middleware-bytebuddy.jar
 * Javassist：-javaagent:E:\itstack\git\github.com\MonitorDesign\cn-bugstack-middleware-javassist\target\cn-bugstack-middleware-javassist.jar
 */
@SpringBootApplication
@Configuration
public class ApiTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiTestApplication.class, args);
    }

}
