package org.evgeny.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.mapper.MapperJSONArrToSetOfGameShort;
import org.evgeny.model.GameShortInformationModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameShortService {

    @Getter
    private static final GameShortService instance = new GameShortService();

    public final MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort = MapperJSONArrToSetOfGameShort.getINSTANCE();

    public Optional<Set<GameShortInformationModel>> GetGameShortService() throws IOException {
        URL url = new URL("https://api.steampowered.com/ISteamApps/GetAppList/v0001/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Set<GameShortInformationModel> set = new HashSet<>();

        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray jsonArray = jsonObject.getJSONObject("applist")
                    .getJSONObject("apps")
                    .getJSONArray("app");

            set = mapperJSONArrToSetOfGameShort.map(jsonArray);

        }
        return Optional.ofNullable(set);
    }


}
