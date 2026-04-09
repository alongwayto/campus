package com.campus.device.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.campus.device.dao")
public class MyBatisConfig {
}
