package org.evgeny.DTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evgeny.Model.StatusUsersInBot;

@NoArgsConstructor()
@AllArgsConstructor()
@Data
public class GameStoreShareInBotDTO {
    String appId;
    String appName;
    String currency;
    int discount;
    String initialFormatted;
    String finalFormatted;
    StatusUsersInBot status;
}
