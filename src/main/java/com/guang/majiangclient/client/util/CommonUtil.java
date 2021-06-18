package com.guang.majiangclient.client.util;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.entity.PlayGameInfo;
import com.guang.majiangclient.client.layout.ClientLayout;

/**
 * 校验数据格式或者数据包是否符合要求
 *
 * @ClassName CheckUtil
 * @Author guangmingdexin
 * @Date 2021/6/10 10:25
 * @Version 1.0
 **/
public class CommonUtil {


    public static final String HEART_BEAT = "heart beat!";

    public static final int READ_TIME_OUT = 3;

    public static final int WRITE_TIME_OUT = 3;

    public static final int READ_WRITE_TIME_OUT = 10;

    /**
     * 校验点击事件时发送的游戏包是否正确
     *
     * @param info 游戏包
     * @return
     */
    public static void checkSpecialEvent(PlayGameInfo info) {
        if(info == null) {
            throw new IllegalArgumentException("数据包错误！");
        }

    }

    public static void markerUi(Direction direction) {
        if(direction == CacheUtil.getGameUserInfo().getDirection()) {
            ClientLayout.bottomMarker.setVisible(true);
        }else {
            ClientLayout.bottomMarker.setVisible(false);
        }
    }
}
