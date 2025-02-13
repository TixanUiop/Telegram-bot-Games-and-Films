package org.evgeny;

import org.evgeny.model.GameShortInformationModel;
import org.evgeny.service.GameShortService;
import org.evgeny.utill.GetProperties;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            String botToken = GetProperties.get("bot.token"); // Получаем токен из свойств
            botsApi.registerBot(new TelegramBot(botToken));
            System.out.println("Бот запущен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
