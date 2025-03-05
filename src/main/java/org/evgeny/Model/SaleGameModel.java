package org.evgeny.Model;

import lombok.*;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleGameModel {
    String appid;
    String name_game;
    String total_price;
    String total_positive;
    String total_negative;
    String total_reviews;
}
