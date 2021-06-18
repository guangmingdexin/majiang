package com.guang.majiangclient.client.handle.log;

import com.guang.majiangclient.client.entity.PlayGameInfo;

import java.util.LinkedList;

/**
 * @ClassName Log
 * @Author guangmingdexin
 * @Date 2021/6/8 11:11
 * @Version 1.0
 **/
public class GameLog {

    private final static LinkedList<PlayGameInfo> log = new LinkedList<>();

    public static boolean offer(PlayGameInfo info) {
        return log.offer(info);
    }


    public  PlayGameInfo poll() {
        if(!log.isEmpty()) {
            return log.poll();
        }
        return null;
    }


    public static PlayGameInfo getLast() {
        return log.getLast();
    }

    public static String print() {
        return log.toString();
    }
}
