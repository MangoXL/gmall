package com.atguigu.gmall.cas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oauth.weibo")
@Data
public class WeiboOAuthConfig {

    private String appKey;
    private String appSecret;
    private String successUrl;
    private String cancelUrl;
    private String authPage;
    private String accessTokenPage;
}
