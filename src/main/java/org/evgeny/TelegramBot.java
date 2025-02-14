package org.evgeny;


import org.evgeny.util.GetProperties;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {


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
                case "Фильмы": {
                    break;
                }
                case "Скидки Steam": {
                    break;
                }
                case "Настройки": {
                    sendSettingsKeyboard(chatId);
                    break;
                }
                default: {
                    sendKeyboardReplyKeyboard(chatId);
                    break;
                }
            }
        }

    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет: " + name + ". \n" +
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
            e.printStackTrace();
        }
    }

    //ReplyKeyboard
    public void sendKeyboardReplyKeyboard(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Скидочки или может быть фильмы?");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Автоматически подстраивается под экран

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Фильмы");
        row1.add("Скидки Steam");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Настройки");

        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //InlineKeyboard settings
    private void sendSettingsKeyboard(long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText("Выберите действие:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard= new ArrayList<>();


        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton buttonSaleGame = new InlineKeyboardButton();
        buttonSaleGame.setText("Еще ничего нет");
        buttonSaleGame.setCallbackData("test");

        InlineKeyboardButton buttonFilms = new InlineKeyboardButton();
        buttonFilms.setText("Тут тоже ничего нет");
        buttonFilms.setCallbackData("test2");

        row1.add(buttonSaleGame);
        row1.add(buttonFilms);
        inlineKeyboard.add(row1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        }
        catch (Exception e) {
            e.printStackTrace();
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
