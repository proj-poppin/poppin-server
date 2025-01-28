package com.poppin.poppinserver.core.config;

import com.poppin.poppinserver.PoppinServerApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = PoppinServerApplication.class)
public class FeignClientConfig {

}
