package com.guang.majiangclient.client.entity;

import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName PlayTakeOutCardInfo
 * @Author guangmingdexin
 * @Date 2021/5/31 20:37
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayGameInfo implements Serializable {

    private long roomId;

    private long userId;

    /**
     * 引起的 value 值
     */
    private int value;

    /**
     * 发送消息的玩家方位
     */
    private Direction direction;

    /**
     * 游戏阶段
     */
    private GameEvent event;

    /**
     * 特殊事件
     */
    private int res;

    /**
     * 具体事件操作
     */
    private int oper;

    /**
     * 特殊事件响应包(true 代表有特殊事件， false 代表无特殊事件)
     */
    private boolean ack;

    public PlayGameInfo(int value, GameEvent event, long roomId, long userId) {
        this.value = value;
        this.event = event;
        this.roomId = roomId;
        this.userId = userId;
    }


    public PlayGameInfo(GameEvent event) {
        this.event = event;
    }

    public PlayGameInfo(GameEvent event, long roomId) {
        this.event = event;
        this.roomId = roomId;
    }


    @Override
    public String toString() {
        return "PlayGameInfo{" +
                "roomId=" + roomId +
                ", userId=" + userId +
                ", value=" + value +
                ", direction=" + direction +
                ", event=" + event +
                ", res=" + res +
                ", oper=" + oper +
                ", ack=" + ack +
                '}';
    }
}
