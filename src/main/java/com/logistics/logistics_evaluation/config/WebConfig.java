package com.logistics.logistics_evaluation.config;

import com.logistics.logistics_evaluation.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")                      // 拦截所有路径
                .excludePathPatterns("/user/login", "/user/register",  // 放行登录注册
                        "/css/**", "/js/**", "/images/**", // 放行静态资源
                        "/upload/**");
    }
}


