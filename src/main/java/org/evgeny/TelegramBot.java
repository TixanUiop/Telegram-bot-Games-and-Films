package org.evgeny;

import org.evgeny.utill.GetProperties;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    //private final String TOKEN_NAME_BOT = "bot.token";
    private final String NAME_BOT = "bot.name";

    public TelegramBot(String token) {
        super(token);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            switch (messageText) {
                case "/start": {
                    startCommandReceived(chatId, userName);
                }
                default: {

                }
            }
        }

    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здаврова: " + name + ". \n" +
                "В данный момент этот бот поможет найти нужную игру в стим";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotUsername() {
        return GetProperties.get(NAME_BOT);
    }

}
