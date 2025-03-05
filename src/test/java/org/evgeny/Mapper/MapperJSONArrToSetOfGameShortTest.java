package org.evgeny.Mapper;

import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Model.SaleGameModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapperJSONArrToSetOfGameShortTest {

    private static MapperJSONArrToSetOfGameShort INSTANCE;

    @BeforeEach
    void setUp() {
        INSTANCE = MapperJSONArrToSetOfGameShort.getINSTANCE();
    }

    @Test
    void mapParseArray() {

        JSONObject jsonObject = new JSONObject(
                """
                    {"applist":{"apps":{"app":[{
                        "appid": 2006441,
                        "name": ""
                    },
                    {
                        "appid": 2005791,
                        "name": ""
                    },
                    {
                        "appid": 216938,
                        "name": "Pieterw test app76 ( 216938 )"
                    } ]}}}
                """
        );
        JSONArray jsonArray = jsonObject.getJSONObject("applist")
                .getJSONObject("apps")
                .getJSONArray("app");



        Set<GameShortInformationModel> expectedResult  = new HashSet<GameShortInformationModel>();

        expectedResult.add(new GameShortInformationModel("2006441", ""));
        expectedResult.add(new GameShortInformationModel("2005791", ""));
        expectedResult.add(new GameShortInformationModel("216938", "Pieterw test app76 ( 216938 )"));


        Set<GameShortInformationModel> actual = INSTANCE.map(jsonArray);

        assertEquals(3, expectedResult.size());
        assertEquals(expectedResult, actual);


    }



    @Test
    void mapSaleGameModel() {

        JSONObject jsonObject = new JSONObject(
        """
            {
                "success": 1,
                    "query_summary": {
                "num_reviews": 0,
                        "review_score": 0,
                        "review_score_desc": "Нет обзоров",
                        "total_positive": 0,
                        "total_negative": 0,
                        "total_reviews": 0
            },
                "reviews": [],
                "cursor": "*"
            }
        """
        ).getJSONObject("query_summary");


        GameShortInformationModel gameShortInformationModel = new GameShortInformationModel("216938", "Pieterw test app76 ( 216938 )");

        SaleGameModel expectedResult = new SaleGameModel(
                gameShortInformationModel.getAppid(),
                gameShortInformationModel.getName(),
                "asdasd",
                "0",
                "0",
                "0"
        );
        SaleGameModel actualResult = INSTANCE.map(jsonObject, gameShortInformationModel);

        Assertions.assertEquals(expectedResult, actualResult);
    }
}