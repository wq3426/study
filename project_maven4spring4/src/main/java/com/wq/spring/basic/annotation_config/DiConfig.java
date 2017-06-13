package com.wq.spring.basic.annotation_config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration //1 声明当前类是一个配置类
@ComponentScan("com.wq.spring.basic.annotation_config")//2 自动扫描包名下的注解类并实例化注入到Spring容器中
public class DiConfig {

}
