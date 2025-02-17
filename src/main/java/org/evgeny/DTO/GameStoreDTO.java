package org.evgeny.DTO;


import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Builder
@Data
@Value
public class GameStoreDTO {
    String appId;
    String appName;
    String currency;
    int discount;
    String initialFormatted;
    String finalFormatted;
}
