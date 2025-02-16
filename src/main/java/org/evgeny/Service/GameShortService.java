package org.evgeny.Service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.Mapper.MapperJSONArrToSetOfGameShort;
import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Model.SaleGameModel;
import org.evgeny.Util.GetProperties;
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

    private final MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort = MapperJSONArrToSetOfGameShort.getINSTANCE();



    public GameShortInformationModel isCorrectGameName(String name) throws IOException {
        URL url = new URL(GetProperties.get("api.all.games"));
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

           return tryFindGame(name, set);
        }
    }

    private GameShortInformationModel tryFindGame(String name, Set<GameShortInformationModel> set) {
//        return  set.stream()
//                .anyMatch(game -> game.getName().equals(name));

        Optional<GameShortInformationModel> findByNameOrId = set.stream()
                .filter(game -> game.getName().equalsIgnoreCase(name) || String.valueOf(game.getAppid()).equals(name))
                .map(game -> new GameShortInformationModel(
                        game.getAppid(),
                        game.getName()
                ))
                .findFirst();


        return findByNameOrId.orElse(null);

        //long findByNameResult = findByName.map(gameShortInformationModel -> Long.parseLong(gameShortInformationModel.getAppid())).orElse(-1L);
        //return set.stream()
        //       .allMatch(gameShortInformationModel -> gameShortInformationModel.getName().equals(name));
    }

    private Optional<Set<GameShortInformationModel>> GetGameShortService() throws IOException {
        URL url = new URL(GetProperties.get("api.all.games"));
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

    public Optional<Set<SaleGameModel>> GetFullGames() {
        try {
            Optional<Set<GameShortInformationModel>> gameShortInformationModels = GetGameShortService();
            SaleGameModel saleGameModel = new SaleGameModel();
            Set<SaleGameModel> set = new HashSet<>();

            if (gameShortInformationModels.isPresent()) {

                for (GameShortInformationModel gameShortInformationModel : gameShortInformationModels.get()) {
                    URL url = new URL(GetProperties.get("api.review").replace("!", gameShortInformationModel.getAppid()));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    StringBuilder stringBuilder = new StringBuilder();

                    try (Scanner scanner = new Scanner(connection.getInputStream())) {
                        while (scanner.hasNext()) {
                            stringBuilder.append(scanner.nextLine());
                        }

                        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                        JSONObject jsonObject1 = jsonObject.getJSONObject("query_summary");


                        saleGameModel = mapperJSONArrToSetOfGameShort.map(jsonObject1, gameShortInformationModel);
                        set.add(saleGameModel);
                        System.out.println(saleGameModel);
                    }
                }

                return Optional.ofNullable(set);
            }
            else {
                return Optional.empty();
            }


        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
