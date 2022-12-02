package com.web.banking.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanConfig {

  @Bean
  ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
