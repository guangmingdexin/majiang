package com.guang.majiangclient.client.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.GameEvent;
import com.guang.majiangclient.client.handle.log.GameLog;
import com.guang.majiangserver.game.PlayGameHandCardsInfo;
import com.guang.majiangserver.game.ServerGameLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName Room
 * @Author guangmingdexin
 * @Date 2021/4/22 8:53
 * @Version 1.0
 *
 * 游戏房间类 临时保存一组玩家信息
 *
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room implements Serializable {

    private final static long serializableUid = 167228187366489L;

    private long roomId;

    private HashSet<GameUser> players;

    // 游戏状态
    private GameEvent gameEvent;

    // 其他信息

    // 准备玩家人数
    @JsonIgnore
    private AtomicInteger waitNum;

    /**
     * 数字手牌
     * 为了隐藏其他玩家的手牌信息，添加此字段
     * 服务器返回玩家信息时，就可以根据不同玩家，设置不同值
     * 而不用考虑如何隐藏房间中其他玩家信息
     */
    private List<Integer> numCards;

    // 特殊事件
    private int res;

    // 保存特殊事件的 信息
    @JsonIgnore
    private transient LinkedList<PlayGameInfo> specialEventInfos;

    @JsonIgnore
    private transient PlayGameHandCardsInfo gameInfos;

    @JsonIgnore
    private transient ServerGameLog log;

    /**
     * 当前玩家回合
     */
    private Direction around;


    public Room(long roomId, HashSet<GameUser> players, GameEvent gameEvent, AtomicInteger waitNum) {
        this.roomId = roomId;
        this.players = players;
        this.gameEvent = gameEvent;
        this.waitNum = waitNum;
    }


    public GameUser findGameUser(long userId) {
        if(players != null && !players.isEmpty()) {
            for (GameUser player : players) {
                if(player.getUserId() == userId) {
                    return player;
                }
            }
        }
        return null;
    }

    public GameUser findGameUser(Direction dire) {
        if(players != null && !players.isEmpty()) {
            for (GameUser player : players) {
                if(player.getDirection() == dire) {
                    return player;
                }
            }
        }
        return null;
    }


    public Direction findCurAroundUser() {
        if(players != null && !players.isEmpty()) {
            for (GameUser player : players) {
                if(player.gameUserIsAround()) {
                    return player.getDirection();
                }
            }
        }
        return null;
    }

    public void nextAround(Direction direction) {
        players.forEach((gameUser -> gameUser.getGameInfoCard().setAroundPlayerDire(direction)));
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", players=" + players +
                ", gameEvent=" + gameEvent +
                ", numCards=" + numCards +
                '}';
    }
}
