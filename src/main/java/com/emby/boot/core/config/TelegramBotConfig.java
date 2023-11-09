package com.emby.boot.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * @author laojian
 * @date 2023/8/27 20:34
 */
@Configuration
public class TelegramBotConfig {
    /**
     * 判断是否需要使用本地代理
     */
    @Value("${spring.telegrambot.defaultBotOptions.enabled:false}")
    private boolean isProxyEnabled;
    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions botOptions = new DefaultBotOptions();

        if (isProxyEnabled) {
            botOptions.setProxyHost("127.0.0.1");
            botOptions.setProxyPort(7890);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        }

        return botOptions;
    }

}
