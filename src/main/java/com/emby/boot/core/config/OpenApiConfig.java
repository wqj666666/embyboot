package com.emby.boot.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenApi 配置类
 *
 * @author laojian
 * @date 2022/9/1
 */
@Configuration
@Profile("dev")
@OpenAPIDefinition(info = @Info(title = "电报emby机器人接口文档", version = "v3.2.0", license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")))
public class OpenApiConfig {
}
