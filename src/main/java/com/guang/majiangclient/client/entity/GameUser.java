package com.guang.majiangclient.client.entity;

import com.guang.majiangclient.client.common.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName GameUser
 * @Author guangmingdexin
 * @Date 2021/5/15 17:07
 * @Version 1.0
 **/


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameUser implements Comparable<GameUser>, Serializable {


    private long userId;

    private String userName;

    private Direction direction;

    // 开始匹配时间
    private long startMatchTime;

    // 结束匹配时间
    private long endMatchTime;

    // 绑定一个 Channel 同服务器进行通信
    private String channelId;

    // 保留玩家游戏棋牌信息
    private GameInfoCard gameInfoCard;

    // 玩家头像 base64编码
    private String base64;

    private Date createTime;

    public GameUser(long userId, String userName, Direction direction, long startMatchTime, long endMatchTime, String channelId) {
        this.userId = userId;
        this.userName = userName;
        this.direction = direction;
        this.startMatchTime = startMatchTime;
        this.endMatchTime = endMatchTime;
        this.channelId = channelId;
    }

    public GameUser(long userId, Date createTime) {
        this.userId = userId;
        this.createTime = createTime;
    }

    public boolean gameUserIsAround() {
        if(gameInfoCard != null) {
            return gameInfoCard.around();
        }
        return false;
    }


    @Override
    public int compareTo(GameUser o) {
        if(startMatchTime >= o.startMatchTime) {
            return 1;
        }else {
            return -1;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj instanceof GameUser) {
            GameUser user = (GameUser) obj;
            return user.userId == userId && user.userName.equals(userName)
                    && user.startMatchTime == startMatchTime && user.endMatchTime == endMatchTime
                    && user.channelId.equals(channelId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return longHash(userId) + (userName == null ? 0 : userName.hashCode()) + longHash(startMatchTime)
                + longHash(endMatchTime) + (channelId == null ? 0 : channelId.hashCode());
    }

    @Override
    public String toString() {
        return "GameUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", direction=" + direction +
                ", startMatchTime=" + startMatchTime +
                ", endMatchTime=" + endMatchTime +
                ", channelId='" + channelId + '\'' +
                ", gameInfoCard=" + gameInfoCard +
                ", base64=" + ((base64 == null) ? "" : base64.substring(0, 10)) +
                '}';
    }

    public long matchTime() {
        return endMatchTime - startMatchTime;
    }

    private int longHash(long param) {
        return (int)(param ^ (param >>> 32));
    }
}
