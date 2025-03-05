package org.evgeny.Service;

import org.evgeny.DTO.GameStoreDTO;
import org.evgeny.Exception.ParseData;
import org.evgeny.Mapper.MapperGameStoreDTOToModel;
import org.evgeny.Mapper.MapperJSONArrToSetOfGameShort;
import org.evgeny.Model.GameInStoreModel;
import org.evgeny.Model.GameShortInformationModel;
import org.evgeny.Util.GetProperties;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

class GameShortServiceTest {

    private MapperJSONArrToSetOfGameShort mapperJSONArrToSetOfGameShort;
    private MapperGameStoreDTOToModel mapperGameStoreDTOToModel;
    private HttpService httpService;

    private GameShortService gameShortService;

    @BeforeEach
    void setUp() {
        mapperJSONArrToSetOfGameShort = Mockito.mock(MapperJSONArrToSetOfGameShort.class);
        mapperGameStoreDTOToModel = Mockito.mock(MapperGameStoreDTOToModel.class);
        httpService = Mockito.mock(HttpService.class);

        gameShortService = new GameShortService(
                mapperJSONArrToSetOfGameShort,
                mapperGameStoreDTOToModel,
                httpService
        );
    }

    @Test
    void getGamePriceById_Success() throws Exception {
        GameShortInformationModel gameShortInformationModel = new GameShortInformationModel(
                "12345",
                "Test"
        );

        String mockJsonResponse = """
        {
             "12345": {
                 "data": {
                     "price_overview": {
                         "currency": "USD",
                         "discount_percent": 10,
                         "initial_formatted": "$60.00",
                         "final_formatted": "$54.00"
                     }
                 }
             }
         }
        """;

        Mockito.doReturn(mockJsonResponse).when(httpService)
                .getJson(GetProperties.get("api.final.price")
                        .replace("!", gameShortInformationModel.getAppid()));

        GameStoreDTO gameStoreDTO = GameStoreDTO.builder()
                .appId("12345")
                .appName("Test")
                .currency("USD")
                .discount(10)
                .initialFormatted("$60")
                .finalFormatted("$54.00")
                .build();

        GameInStoreModel gameInStoreModelExpected = GameInStoreModel.builder()
                .appId(gameStoreDTO.getAppId())
                .appName(gameStoreDTO.getAppName())
                .currency(gameStoreDTO.getCurrency())
                .discount(gameStoreDTO.getDiscount())
                .initialFormatted(gameStoreDTO.getInitialFormatted())
                .finalFormatted(gameStoreDTO.getFinalFormatted())
                .build();

        Mockito.doReturn(gameInStoreModelExpected).when(mapperGameStoreDTOToModel).map(Mockito.any());

        GameInStoreModel actualResult = gameShortService.getGamePriceById(gameShortInformationModel);

        assertNotNull(actualResult);
        assertEquals(gameInStoreModelExpected, actualResult);
        Mockito.verify(httpService, times(1)).getJson(anyString());
    }
    @Test
    void getGamePriceById_IfIdDoesNotExists() throws Exception {
        GameShortInformationModel gameShortInformationModel = new GameShortInformationModel(
                "213",
                "Test"
        );
        Mockito.doReturn("{Invalid Json}").when(httpService).getJson(anyString());
        assertThrows(ParseData.class, () -> gameShortService.getGamePriceById(gameShortInformationModel));

    }

    @Test
    void isCorrectGameName_Success() throws Exception {
        Set<GameShortInformationModel> set = new HashSet<>();
        set.add(new GameShortInformationModel(
                "123",
                "Test"
        ));

        String responseJson = """
                {
                "applist":{"apps":{"app":[{
                	"appid": 286690,
                	"name": "Metro 2033 Redux"
                },
                {
                	"appid": 286730,
                	"name": "Gunship!"
                }]
                } }}
                """;

        Mockito.doReturn(responseJson).when(httpService).getJson(anyString());
        Mockito.doReturn(set).when(mapperJSONArrToSetOfGameShort).map(Mockito.any(JSONArray.class));

        GameShortInformationModel actualResult = gameShortService.isCorrectGameName("test");

        assertNotNull(actualResult);
        assertEquals("123", actualResult.getAppid());

    }
    @Test
    void isCorrectGameName_Failed() throws Exception {
        Set<GameShortInformationModel> set = new HashSet<>();
        set.add(new GameShortInformationModel(
                "test",
                "test"
        ));
        String responseJson = """
                {
                "applist":{"apps":{"app":[{
                	"appid": 286690,
                	"name": "Metro 2033 Redux"
                },
                {
                	"appid": 286730,
                	"name": "Gunship!"
                }]
                } }}
                """;
        Mockito.doReturn(responseJson).when(httpService).getJson(anyString());
        Mockito.doReturn(set).when(mapperJSONArrToSetOfGameShort).map(Mockito.any(JSONArray.class));
        GameShortInformationModel actualResult = gameShortService.isCorrectGameName("test1");
        assertNull(actualResult);

    }


    @Test
    void getFullGames() {
    }
}