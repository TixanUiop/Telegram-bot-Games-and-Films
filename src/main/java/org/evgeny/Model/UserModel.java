package org.evgeny.Model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;


@Builder
@Data
public class UserModel {
    long id;
    BigInteger user_id;
    BigInteger game_id;
    String game_name;
    BigDecimal target_price;
    StatusGameFinder status;
    LocalDate created_at;
}
