package org.evgeny.Mapper;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.DTO.GameStoreDTO;
import org.evgeny.Model.GameInStoreModel;
import org.telegram.telegrambots.meta.api.objects.games.Game;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapperGameStoreDTOToModel implements IMapper<GameStoreDTO, GameInStoreModel> {
    @Getter
    private static final MapperGameStoreDTOToModel INSTANCE = new MapperGameStoreDTOToModel();

    private String DEFAULT_VALUE = "Бесплатно";
    @Override
    public GameInStoreModel map(GameStoreDTO from) {
        return GameInStoreModel.builder()
                .appId(from.getAppId())
                .appName(from.getAppName())
                .currency(from.getCurrency())
                .discount(from.getDiscount())
                .initialFormatted(
                        (from.getInitialFormatted() != null && !from.getInitialFormatted().isEmpty())
                                ? from.getInitialFormatted()
                                : from.getFinalFormatted()
                )
                .finalFormatted(
                        (from.getFinalFormatted() != null && !from.getFinalFormatted().isEmpty())
                                ? from.getFinalFormatted()
                                : DEFAULT_VALUE
                )
                .build();
    }
}
