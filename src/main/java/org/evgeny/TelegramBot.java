package org.evgeny;


import org.evgeny.DTO.GameStoreShareInBotDTO;
import org.evgeny.Exception.ParseData;
import org.evgeny.Model.*;
import org.evgeny.Service.GameShortService;
import org.evgeny.Service.UserGameService;
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


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TelegramBot extends TelegramLongPollingBot {



    private final String NAME_BOT = "bot.name";
    private Map<Long, String> userStates = new ConcurrentHashMap<>();
    private final GameShortService gameShortService = GameShortService.getInstance();
    private Map<Long ,GameInStoreModel> usersDateBeforeAddedIntoDataBase = new ConcurrentHashMap<>();
    private final UserGameService userGameService = UserGameService.getInstance();
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

            if (userStates.containsKey(chatId) && userStates.get(chatId).equals(StatusUsersInBot.WAIT_INPUT_GAME.name())) {
                userStates.remove(chatId);
                processedAddButtonClick(chatId, messageText);
                return;
            }
            //Add price
            if (userStates.containsKey(chatId) && userStates.get(chatId).equals(StatusUsersInBot.WAIT_INPUT_PRICE.name())
                    && usersDateBeforeAddedIntoDataBase.containsKey(chatId)) {
                userStates.remove(chatId);
                GameInStoreModel gameInStoreModel = usersDateBeforeAddedIntoDataBase.get(chatId);
                usersDateBeforeAddedIntoDataBase.remove(chatId);
                processedAddButtonClickSetPrice(chatId, messageText, gameInStoreModel);
                return;
            }
            //look btn
            if (userStates.containsKey(chatId) && userStates.get(chatId).equals(StatusUsersInBot.WAIT_INPUT_LOOK.name())
                    && usersDateBeforeAddedIntoDataBase.containsKey(chatId)) {
                userStates.remove(chatId);
                return;
            }

            if (messageText.replace("<", "").replace(">", "").startsWith("/remove")) {
                Pattern pattern = Pattern.compile("^/remove\\s+(\\d+)$");
                Matcher matcher = pattern.matcher(messageText.trim());

                if (matcher.matches()) {
                    String appId = matcher.group(1);

                    sendMessage(chatId, """
                        ⏳ *Идёт процесс удаления записи* \s
                    """);
                    // Логика для удаления
                    if (userGameService.getDeleteById(BigInteger.valueOf(Long.parseLong(appId)))) {
                        sendMessage(chatId, """
                            ✅ *Игра успешно удалена из списока мониторинга цен!* \s
                        """);
                    }
                    else {
                        sendMessage(chatId, """
                            ❌ *Не удалось удалить запись мониторинга.* \n
                            📌 Возможно запись уже *удалена* или ID введен неверно.
                         """);
                    }
                } else {
                    sendMessage(chatId, """
                    ❌ *Команда не распознана или ID неверен.* \s
                 """);
                }
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

        if (data.equals("add"))
        {
            userStates.put(userId, StatusUsersInBot.WAIT_INPUT_GAME.name());
        }
        if (data.equals("look"))
        {
            userStates.put(userId, StatusUsersInBot.WAIT_INPUT_LOOK.name());
        }


        switch (data) {
            case "add":
                sendMessage(userId, """
                        ✍️ *Введите название игры корректно*, чтобы бот мог ее найти в Steam, \s
                        или укажите ее *уникальный идентификатор* (App ID). \s
                        
                        🔍 Узнать его можно тут: [SteamDB](https://steamdb.info/apps/).
                        """);
                break;
            case "look":
                processedGetUserWatchlist(BigInteger.valueOf(userId));
                break;
        }
    }

    private void processedGetUserWatchlist(BigInteger userId) {
        Optional<List<UserModel>> allUsersGames = userGameService.getAllUsersGames(userId);

        allUsersGames.ifPresentOrElse(
                games ->
                {
                    String gameList = games.stream()
                    .map(game -> """
                    🎮 *Название:* %s
                    💰 *Цель:* %.2f %s
                    🆔 *App ID:* %s
                    """.formatted(game.getGame_name(), game.getTarget_price(), "$", game.getGame_id()))
                    .collect(Collectors.joining("──────────────────\n"));

                    sendMessage(userId.longValue(), """
                    📋 *Ваш список отслеживаемых игр:* \s
                   
                    %s
                   
                    ✏️ Чтобы *добавить* новую игру в список, используйте пункт добавление. \s\n
                    ❌ Чтобы *удалить* игру из списка, используйте команду `/remove <App ID>`. \s
                   
                    🔍 Узнать App ID можно тут: [SteamDB](https://steamdb.info/apps/).
                    """.formatted(
                            gameList.isEmpty() ? "*Список пуст*" : gameList
                    ));
                },
                () -> sendMessage(userId.longValue(), """
                    ❌ *У вас нет ни одной игры в списке мониторинга* \s
                    📌 Перейдите в пункт *Steam* → *Добавить* и следуйте инструкциям.
                 """)
        );
    }

    private void processedAddButtonClickSetPrice(long userId, String price, GameInStoreModel model) {
        try {
            BigDecimal result = BigDecimal.valueOf(Double.parseDouble(price));
            boolean res = userGameService.addGame(
                    UserModel.builder()
                            .user_id(BigInteger.valueOf(userId))
                            .game_id(BigInteger.valueOf(Long.parseLong(model.getAppId())))
                            .game_name(model.getAppName())
                            .target_price(result)
                            .status(StatusGameFinder.waiting)
                            .created_at(LocalDate.now())
                            .build()
            );
            if (res) {
                sendMessage(userId, """
                ✅ *Игра успешно добавлена в список мониторинга цен!* \s
                """);
                sendKeyboardReplyKeyboard(userId);
            }
            else {
                sendMessage(userId, """
                ❌ *Игра уже есть в списке ожидания* \s
                🔎 Проверьте правильность написания и попробуйте снова следуя примера написания стоимости.
                """);
            }
        }
        catch (IllegalArgumentException e) {
            sendMessage(userId, """
                ❌ *Цена указана неверно!* \s
                🔎 Возможно, вы ошиблись в формате ввода стоимости. \s
                Проверьте правильность написания и попробуйте снова следуя примера написания стоимости. \s
                🔹 Примеры ввода цены:
                               ✔️ 2.3
                               ✔️ 10.30
                               ✔️ 0.90
                """);
            userStates.put(userId, StatusUsersInBot.WAIT_INPUT_PRICE.name());
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

                try {
                    GameInStoreModel gamePriceById = gameShortService.getGamePriceById(correctGameName);

                    sendMessage(userId, """
                ✅ *Игра найдена!*
                📌 *Название:* *%s* 🎮
                🆔 *ID:* *%s*
                💰 *Цена в данный момент:* %s
                💰 *Цена без скидки:* %s
                🔥 *Скидка:* - %d%%
                """.formatted(
                            correctGameName.getName(),
                            correctGameName.getAppid(),
                            gamePriceById.getFinalFormatted(),
                            gamePriceById.getInitialFormatted(),
                            gamePriceById.getDiscount()
                    ));

                    sendMessage(userId, """
                           📢 Укажите желаемую цену! 💰
                        
                           Введите сумму, по которой хотите приобрести игру.
                           📉 *Когда её стоимость опустится или достигнет указанного уровня, мы сразу же пришлём вам уведомление!* 🔔🎮
    
                           🔹 Примеры ввода цены:
                           ✔️ 2.3
                           ✔️ 10.30
                           ✔️ 0.90
                           """);

                    userStates.put(userId, StatusUsersInBot.WAIT_INPUT_PRICE.name());
                    usersDateBeforeAddedIntoDataBase.put(userId, gamePriceById);

                }
                catch (ParseData pd) {
                    sendMessage(userId, """
                ✅ *Игра найдена!*
                📌 *Название:* *%s* 🎮
                🆔 *ID:* *%s*
                💰 *Цена в данный момент:* %s
                """.formatted(
                            correctGameName.getName(),
                            correctGameName.getAppid(),
                            "Бесплатно"
                    ));
                }
            }
            else {
                sendMessage(userId, """
                        ❌ *Игра не найдена!* \s\n
                        🔎 Возможно, вы ошиблись в названии или такой игры в Steam нет. \s
                        Проверьте правильность написания и попробуйте снова. \s\n
                        """);
                sendKeyboardReplyKeyboard(userId);
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
                
                🔹 Чтобы добавить игру в список отслеживания, выбери *Steam* → *Добавить*. \s\n
                🔹 Для просмотра списка активных ожиданий – *Steam* → *Просмотреть*. \s\n
                🔹 Чтобы удалить игру из мониторинга – *Steam* → *Удалить*. \s\n
                
                Также ты можешь узнать актуальные *киноафиши Гомеля* 🎬 или изменить настройки ⚙️ в соответствующем разделе. \s
                
                🚀 Выбери нужный пункт в меню и начнем!
                
                """.formatted(name);
        sendMessage(chatId, answer);
        sendKeyboardReplyKeyboard(chatId);
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

        row1.add(buttonAddGame);
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
