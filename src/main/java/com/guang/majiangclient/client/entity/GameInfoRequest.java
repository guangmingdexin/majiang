package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guang.majiangclient.client.common.enums.GameEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * @ClassName GameInfo
 * @Author guangmingdexin
 * @Date 2021/4/22 9:07
 * @Version 1.0
 *
 *  游戏包资源
 **/
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameInfoRequest {

    // 首先需要包含 玩家信息（玩家 id， 玩家用户名，玩家头像）
    // TODO 简单起见 先不使用 内部类 , 后面应该还需要添加一些棋牌类
    private GameUser user;

    private PlayGameInfo info;


    public GameInfoRequest(GameUser user, PlayGameInfo info) {
        this.user = user;
        this.info = info;
    }

    public GameInfoRequest(PlayGameInfo gameInfo) {
        this.info = gameInfo;
    }

}
