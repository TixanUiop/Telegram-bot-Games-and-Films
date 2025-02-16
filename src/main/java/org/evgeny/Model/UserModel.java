package org.evgeny.Model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;


@Builder
@Data
public class UserModel {
    int id;
    String user_id;
    String game_id;
    String game_name;
    BigDecimal target_price;
    StatusGameFinder status;
    LocalTime created_at;
}
