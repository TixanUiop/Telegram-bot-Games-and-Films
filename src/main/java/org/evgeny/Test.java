package org.evgeny;


import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Service.GameShortService;
import org.evgeny.Util.GetProperties;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Test {
    public static void main(String[] args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            String botToken = GetProperties.get("bot.token");
            botsApi.registerBot(new TelegramBot(botToken));
            System.out.println("Бот запущен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
