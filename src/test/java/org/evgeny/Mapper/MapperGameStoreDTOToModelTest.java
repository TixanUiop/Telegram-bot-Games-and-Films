package org.evgeny.Mapper;

import org.evgeny.DTO.GameStoreDTO;
import org.evgeny.Model.GameInStoreModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapperGameStoreDTOToModelTest {
   private final MapperGameStoreDTOToModel mapper = MapperGameStoreDTOToModel.getINSTANCE();


   @Test
   void mapIfHappyPath() {
       GameStoreDTO gameStoreDTO = getGameStoreDTO();

       GameInStoreModel result = mapper.map(gameStoreDTO);
       assertEquals(getGameInStoreModel(), result);

   }
   @Test
   void mapIfInitialFormattedIsNull() {
       GameStoreDTO build = GameStoreDTO.builder()
               .appId("123")
               .appName("Terraria")
               .currency("USD")
               .discount(0)
               .initialFormatted("12")
               .finalFormatted("12")
               .build();

       GameInStoreModel result = mapper.map(build);
       assertEquals(build.getFinalFormatted(), result.getInitialFormatted());
   }

    @Test
    void mapIfFinalFormattedIsNull() {
        GameStoreDTO build = GameStoreDTO.builder()
                .appId("123")
                .appName("Terraria")
                .currency("USD")
                .discount(0)
                .initialFormatted("tes")
                .finalFormatted(null)
                .build();

        GameInStoreModel result = mapper.map(build);
        assertEquals("Бесплатно", result.getFinalFormatted());
    }

    
    private GameInStoreModel getGameInStoreModel() {
        return GameInStoreModel.builder()
                .appId("123")
                .appName("Terraria")
                .currency("USD")
                .discount(0)
                .initialFormatted("sdf")
                .finalFormatted("tes")
                .build();

    }

   private GameStoreDTO getGameStoreDTO() {
       return  GameStoreDTO.builder()
               .appId("123")
               .appName("Terraria")
               .currency("USD")
               .discount(0)
               .initialFormatted("sdf")
               .finalFormatted("tes")
               .build();
   }
}