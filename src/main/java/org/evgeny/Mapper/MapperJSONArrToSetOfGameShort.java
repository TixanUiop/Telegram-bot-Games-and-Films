package org.evgeny.Mapper;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Model.SaleGameModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapperJSONArrToSetOfGameShort implements IMapper<JSONArray, Set<GameShortInformationModel>> {

    @Getter
    private static final MapperJSONArrToSetOfGameShort INSTANCE = new MapperJSONArrToSetOfGameShort();

    private GameShortInformationModel gameShortInformationModel;

    @Override
    public Set<GameShortInformationModel> map(JSONArray array) {

        Set<GameShortInformationModel> gameShortInformationModelSet = new HashSet<GameShortInformationModel>();

        for (Object jsonObject : array) {
            JSONObject result = (JSONObject) jsonObject;

            gameShortInformationModel = new GameShortInformationModel(
                    new String(String.valueOf(result.getInt("appid"))),
                    result.getString("name")
            );
            gameShortInformationModelSet.add(gameShortInformationModel);
        }

        return gameShortInformationModelSet;
    }

    //This method is not using
    public SaleGameModel map(JSONObject obj, GameShortInformationModel gameShortInformationModel) {

       return new SaleGameModel(
               gameShortInformationModel.getAppid(),
               gameShortInformationModel.getName(),
               "asdasd",
               //String.valueOf(obj.getInt("total_price")),
               String.valueOf(obj.getInt("total_positive")),
               String.valueOf(obj.getInt("total_negative")),
               String.valueOf(obj.getInt("total_reviews"))
       );
    }
}
