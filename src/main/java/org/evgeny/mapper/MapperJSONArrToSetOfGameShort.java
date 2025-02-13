package org.evgeny.mapper;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.model.GameShortInformationModel;
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

        for (Object jsonObject: array) {
            JSONObject result = (JSONObject) jsonObject;

            gameShortInformationModel = new GameShortInformationModel(
                    result.getInt("appid"),
                    result.getString("name")
            );
            gameShortInformationModelSet.add(gameShortInformationModel);
        }

        return gameShortInformationModelSet;
    }
}
