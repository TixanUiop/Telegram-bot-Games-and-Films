package org.evgeny.Util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class GetPropertiesTest {

    @ParameterizedTest
    @MethodSource("getSuccessParam")
    void getSuccessGetProperties(String key, String value) {
        String result = GetProperties.get(key);
        Assertions.assertEquals(value, result);
    }


    static Stream<Arguments> getSuccessParam() {
        return Stream.of(
                Arguments.of("bot.name", "JavaDemoHelper_bot"),
                Arguments.of("bot.token.test", "123123123123123123123123123123"),
                Arguments.of("api.final.price", "https://store.steampowered.com/api/appdetails?/filters=price_overview&appids=!&RU"),
                Arguments.of("api.all.inf", "https://store.steampowered.com/api/appdetails?appids=!&cc=tw"),
                Arguments.of("api.review", "https://store.steampowered.com/appreviews/!?json=1"),
                Arguments.of("api.all.games", "https://api.steampowered.com/ISteamApps/GetAppList/v0001/"),
                Arguments.of("db.connection.url", "jdbc:postgresql://localhost:5432/telegram_steam_bot"),
                Arguments.of("db.connection.password", "postgres"),
                Arguments.of("db.connection.user", "postgres")
        );
    }




}
