package com.guang.majiangserver.game;

import com.guang.majiangclient.client.entity.PlayGameInfo;
import lombok.Getter;

import java.util.LinkedList;

/**
 * @ClassName ServerGameLog
 * @Author guangmingdexin
 * @Date 2021/6/10 11:19
 * @Version 1.0
 **/
public class ServerGameLog {

    private final static LinkedList<PlayGameInfo> log = new LinkedList<>();

    public boolean offer(PlayGameInfo info) {
        return log.offer(info);
    }


    public  PlayGameInfo poll() {
        if(!log.isEmpty()) {
            return log.poll();
        }
        return null;
    }

    public  PlayGameInfo getLast() {
        return log.getLast();
    }

    public  String print() {
        return log.toString();
    }

    public LinkedList<PlayGameInfo> getLog() {
        return log;
    }
}
