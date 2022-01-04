package ds.guang.majing.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author guangyong.deng
 * @date 2021-12-08 15:48
 */
public final class DsConstant {



    /**
     * 当前版本
     */
    public final static String VERSION = "V3";

    public final static String URL = "http://localhost:9001/";

    /**
     * 用户事件类型
     */
    public final static String USER_EVENT = "USER_EVENT";

    public final static String SYS_EVENT = "SYS_EVENT";


    /**
     * 消息体code
     */
    public static final int CODE_SUCCESS = 200;

    public static final int CODE_ERROR = 500;

    public static final int CODE_WAIT = -1;


    /**
     *  状态 id 常量
     *
     */
    public static final String STATE_LOGIN_ID = "STATE_LOGIN_ID";

    public static final String STATE_PLATFORM_ID = "STATE_PLATFORM_ID";

    public static final String STATE_PREPARE_ID = "STATE_PREPARE_ID";

    public static final String STATE_MATCH_FRIEND_ID = "STATE_MATCH_FRIEND_ID";

    public static final String STATE_INITIAL_ID = "STATE_INITIAL_ID";


    /**
     * 事件 id 常量
     */
    public static final String EVENT_LOGIN_ID = "EVENT_LOGIN_ID";

    public static final String EVENT_INITIAL_ID = "EVENT_INITIAL_ID";

    public static final String EVENT_PLATFORM_ID = "EVENT_PLATFORM_ID";

    public static final String EVENT_PREPARE_ID = "EVENT_PREPARE_ID";

    public static final String EVENT_MATCH_FRIEND_ID = "EVENT_MATCH_FRIEND_ID";

    public static final String EVENT_POST_HANDCARD_ID = "EVENT_POST_HANDCARD_ID";

    /**
     * 设置缓存前缀
     */
    public static final String GAMEUSER_INFO_PREV = "user-game-info-id:";

    public static final String USER_MACHINE_PREV = "user-machine-id:";

    public static final String ROOM_INFO_PREV = "room-info-id:";
    
    public static final String USER_CHANEL_PREV = "user-channel-id:";

    public static String preGameUserInfoKey(String id) {
        return GAMEUSER_INFO_PREV + id;
    }

    public static String preUserMachinekey(String id) {
        return USER_MACHINE_PREV + id;
    }

    public static String preRoomInfoPrev(String id) {
        return ROOM_INFO_PREV + id;
    }

    public static String preUserChanelPrev(String id) {
        return USER_CHANEL_PREV + id;
    }
    /**
     * 系统
     */
    public static final String OS_NAME_KEY = "os.name";

    public static final String OS_LINUX_PREFIX = "linux";

    public static final String OS_WIN_PREFIX = "win";

    /**
     * 一些游戏系统的常见统一变量
     * SYS_CONTEXT : netty ChannelHandlerContext 上下文缓存 key
     */
    public static final String SYS_CONTEXT = "sys-context";

    /**
     *
     * 设置初始麻将
     */
    public static final List<Integer> CARDS = initialCards();

    private static List<Integer> initialCards() {

        // 1.服务器生成 108 张棋牌（只可读）
        List<Integer> cards = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                for (int k = 1; k <= 4; k++) {
                    cards.add((int) (j + Math.pow(10, i)));
                }
            }
        }

        return cards;
    }
}
