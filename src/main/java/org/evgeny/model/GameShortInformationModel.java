package org.evgeny.model;


import lombok.Data;
import lombok.Getter;


@Data
@Getter

public class GameShortInformationModel {
    long appid;
    String name;

    public GameShortInformationModel(long appid, String name) {
        this.appid = appid;
        this.name = name;
    }
}
