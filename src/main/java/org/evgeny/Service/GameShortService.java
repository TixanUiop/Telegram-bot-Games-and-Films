package org.evgeny.Service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.DTO.GameStoreDTO;
import org.evgeny.Exception.ParseData;
import org.evgeny.Mapper.MapperGameStoreDTOToModel;
import org.evgeny.Mapper.MapperJSONArrToSetOfGameShort;
import org.evgeny.Model.GameInStoreModel;
import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Model.SaleGameModel;
import org.evgeny.Util.GetProperties;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

    //private final MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort = MapperJSONArrToSetOfGameShort.getINSTANCE();
    //private final MapperGameStoreDTOToModel mapperGameStoreDTOToModel = MapperGameStoreDTOToModel.getINSTANCE();

    private MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort;
    private MapperGameStoreDTOToModel mapperGameStoreDTOToModel;
    private HttpService httpService;


    public GameShortService(MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort,
                            MapperGameStoreDTOToModel mapperGameStoreDTOToModel,
                            HttpService HttpService) {

        this.mapperJSONArrToSetOfGameShort = mapperJSONArrToSetOfGameShort;
        this.mapperGameStoreDTOToModel = mapperGameStoreDTOToModel;
        this.httpService = HttpService;
    }

    public GameInStoreModel getGamePriceById(GameShortInformationModel game) throws IOException {
        String url = GetProperties.get("api.final.price").replace("!", game.getAppid());
        String json = httpService.getJson(url);

            try {
                JSONObject jsonObject = new JSONObject(json);

                JSONObject priceOverview = jsonObject.getJSONObject(game.getAppid())
                        .getJSONObject("data")
                        .getJSONObject("price_overview");

                GameStoreDTO build = GameStoreDTO.builder()
                        .appId(game.getAppid())
                        .appName(game.getName())
                        .currency(priceOverview.getString("currency"))
                        .discount(priceOverview.getInt("discount_percent"))
                        .initialFormatted(priceOverview.getString("initial_formatted"))
                        .finalFormatted(priceOverview.getString("final_formatted"))
                        .build();

                return mapperGameStoreDTOToModel.map(build);
            }
            catch (Exception e) {
                throw new ParseData(e.getMessage());
            }
    }


    public GameShortInformationModel isCorrectGameName(String name) throws IOException {
        String string = GetProperties.get("api.all.games");

        String json = httpService.getJson(string);
        Set<GameShortInformationModel> set = new HashSet<>();

        JSONObject jsonObject = new JSONObject(json);

        JSONArray jsonArray = jsonObject.getJSONObject("applist")
                .getJSONObject("apps")
                .getJSONArray("app");

        set = mapperJSONArrToSetOfGameShort.map(jsonArray);

       return tryFindGame(name, set);

    }

    private GameShortInformationModel tryFindGame(String name, Set<GameShortInformationModel> set) {
        Optional<GameShortInformationModel> findByNameOrId = set.stream()
                .filter(game -> game.getName().toLowerCase().replace(" ", "").equalsIgnoreCase(name.toLowerCase().replace(" ", "")) || String.valueOf(game.getAppid()).equals(name))
                .map(game -> new GameShortInformationModel(
                        game.getAppid(),
                        game.getName()
                ))
                .findFirst();
        return findByNameOrId.orElse(null);
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


    //TODO need to fix it
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
