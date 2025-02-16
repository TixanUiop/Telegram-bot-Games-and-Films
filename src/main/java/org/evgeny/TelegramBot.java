package org.evgeny;


import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Service.GameShortService;
import org.evgeny.Util.GetProperties;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBot extends TelegramLongPollingBot {

    private final String WAIT = "waiting_input_game";
    private final String NAME_BOT = "bot.name";
    private Map<Long, String> userStates = new ConcurrentHashMap<>();
    private final GameShortService gameShortService = GameShortService.getInstance();


    public TelegramBot(String token) {
        super(token);
    }

    @Override
    public void onUpdateReceived(Update update) {
        //If user has chosen steam option
        if (update.hasCallbackQuery()) {
            handleCallBack(update.getCallbackQuery());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText() || update.hasCallbackQuery()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            if (userStates.containsKey(chatId) && userStates.get(chatId).equals(WAIT)) {
                userStates.remove(chatId);
                processedAddButtonClick(chatId, messageText);
                return;
            }


            switch (messageText) {
                case "/start": {
                    startCommandReceived(chatId, userName);
                    break;
                }
                case "Фильмы": {
                   // films(chatId, userName);
                    break;
                }
                case "Steam": {
                    sendKeyboardSteam(chatId);
                    break;
                }
                case "Настройки": {
                    sendKeyboardSteam(chatId);
                    break;
                }
                default: {
                    sendKeyboardReplyKeyboard(chatId);
                    break;
                }
            }
        }

    }

    private void handleCallBack(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long userId = callbackQuery.getFrom().getId();
        userStates.put(userId, WAIT);

        switch (data) {
            case "add":
                sendMessage(userId, """
                        ✍️ *Введите название игры корректно*, чтобы бот мог ее найти в Steam, \s
                        или укажите ее *уникальный идентификатор* (App ID). \s
                        
                        🔍 Узнать его можно тут: [SteamDB](https://steamdb.info/apps/).
                        """);
                break;

            case "look":
                sendMessage(userId, String.valueOf(userId) + data);
                break;

            case "delete":
                sendMessage(userId, String.valueOf(userId) + data);
                break;
        }


    }

    private void processedAddButtonClick(long userId, String gameName) {
        sendMessage(userId, """
                    🕵️ *Проверка на корректность написания игры...* \s
                    Пожалуйста, убедитесь, что название указано верно. \s
                """);
        try {
            GameShortInformationModel correctGameName = gameShortService.isCorrectGameName(gameName);
            if (correctGameName != null) {
                sendMessage(userId, """
                        ✅ *Игра найдена!* \s
                        📌 Название: *%s* 🎮 \s
                        🆔 ID: *%s* \s
                        """.formatted(correctGameName.getName(), correctGameName.getAppid()));
            }
            else {
                sendMessage(userId, """
                        ❌ *Игра не найдена!* \s
                        🔎 Возможно, вы ошиблись в названии или такой игры в Steam нет. \s
                        Проверьте правильность написания и попробуйте снова. \s
                        """);
            }
        }
        catch (Exception e) {
            sendMessage(userId, """
                    ⚠️ *Ошибка поиска!* \s
                    ⏳ Повторите попытку позже. \s
                    """);
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = """
                Привет, *%s*! 👋 \s
                
                Этот бот поможет тебе отслеживать скидки на игры в *Steam* и сообщит, когда цена опустится до нужного уровня. 🎮🔥 \s
                
                🔹 Чтобы добавить игру в список отслеживания, выбери *Steam* → *Добавить*. \s
                🔹 Для просмотра списка активных ожиданий – *Steam* → *Просмотреть*. \s
                🔹 Чтобы удалить игру из мониторинга – *Steam* → *Удалить*. \s
                
                Также ты можешь узнать актуальные *киноафиши Гомеля* 🎬 или изменить настройки ⚙️ в соответствующем разделе. \s
                
                🚀 Выбери нужный пункт в меню и начнем!
                
                """.formatted(name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setParseMode("Markdown");
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
        message.setText(
                "*Выберите необходимый пункт:* \n\n" +
                "🎬 *Фильмы* – Показывают актуальные киноафиши города Гомель (пока что).\n\n" +
                "🎮 *Steam* – Позволяет сообщить о скидке на выбранную игру, когда она опустится до необходимой отметки.\n\n" +
                "⚙️ *Настройки* – Помогут кое-что подкорректировать."
        );
        message.setParseMode("Markdown");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Автоматически подстраивается под экран

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Фильмы");
        row1.add("Steam");

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

    //InlineKeyboard
    private void sendKeyboardSteam(long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(
                "*Выберите подходящий пункт:* \n\n" +
                "✅ *Добавить* – Добавление необходимой игры в режим мониторинга цен.\n\n" +
                "❌ *Удалить* – Удаление мониторинга необходимого продукта.\n\n" +
                "📋 *Просмотреть* – Просмотр списка действующих ожиданий."
        );
        sendMessage.setParseMode("Markdown");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard= new ArrayList<>();


        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton buttonAddGame = new InlineKeyboardButton();
        buttonAddGame.setText("Добавить");
        buttonAddGame.setCallbackData("add");

        InlineKeyboardButton buttonLookAtList = new InlineKeyboardButton();
        buttonLookAtList.setText("Просмотреть");
        buttonLookAtList.setCallbackData("look");

        InlineKeyboardButton buttonRemove = new InlineKeyboardButton();
        buttonRemove.setText("Удалить");
        buttonRemove.setCallbackData("delete");

        row1.add(buttonAddGame);
        row1.add(buttonRemove);
        row1.add(buttonLookAtList);

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
