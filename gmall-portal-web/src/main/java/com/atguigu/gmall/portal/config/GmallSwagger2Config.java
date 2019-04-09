package com.atguigu.gmall.portal.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class GmallSwagger2Config {

    @Bean("前端检索模块")
    public Docket userApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("前端检索模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("谷粒商城-前端检索系统平台接口文档")
                .description("提供search模块的文档")
                .termsOfServiceUrl("http://www.atguigu.com/")
                .version("1.0")
                .build();
    }


}
