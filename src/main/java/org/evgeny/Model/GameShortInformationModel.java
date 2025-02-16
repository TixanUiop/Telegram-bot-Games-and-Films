package org.evgeny.Model;


import lombok.Data;
import lombok.Getter;


@Data
@Getter
public class GameShortInformationModel {
    String appid;
    String name;

    public GameShortInformationModel(String appid, String name) {
        this.appid = appid;
        this.name = name;
    }
}
