package com.emby.boot;

import com.emby.boot.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
@EnableScheduling
public class TgPikpakApplication implements CommandLineRunner {
    private final TelegramBot telegramBot;

    public static void main(String[] args) {
        SpringApplication.run(TgPikpakApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
//        clearWebhook();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    public void clearWebhook() {
//        String requestUrl = "https://api.telegram.org/bot" + "6449378490:AAFKb2M5xSn4ffbVkSaGntU9sfFjGY07XYg" + "/deleteWebhook";
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
//        if (response.getStatusCode() == HttpStatus.OK) {
//            System.out.println("Webhook 移除成功");
//        } else {
//            System.out.println("Webhook 移除失败");
//        }
//    }

}