package org.evgeny.Model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Data
@Value
@AllArgsConstructor
public class GameInStoreModel {
    String appId;
    String appName;
    String currency;
    int discount;
    String initialFormatted;
    String finalFormatted;
}
