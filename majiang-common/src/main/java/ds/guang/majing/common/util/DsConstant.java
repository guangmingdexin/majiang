package ds.guang.majing.common.util;

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
    public static final String CODE_SUCCESS = "200";

    public static final String CODE_ERROR = "500";

    public static final String CODE_WAIT = "-1";


    /**
     *  状态 id 常量
     *
     */
    public static final String STATE_START_ID = "STATE_START_ID";

    public static final String STATE_PREPARE_ID = "STATE_PREPARE_ID";

    public static final String STATE_MATCH_FRIEND_ID = "STATE_MATCH_FRIEND_ID";


    public static final String STATE_TAKE_CARD_ID = "STATE_TAKE_CARD_ID";

    public static final String STATE_TAKE_OUT_CARD_ID = "STATE_TAKE_OUT_CARD_ID";

    public static final String STATE_WAIT_ID = "STATE_WAIT_ID";

    public static final String STATE_EVENT_ID = "STATE_EVENT_ID";

    /**
     * 本局游戏结束的状态
     */
    public static final String STATE_GAME_OVER_ID = "STATE_GAME_OVER_ID";

    /**
     * 事件 id 常量
     */
    public static final String EVENT_START_ID = "EVENT_START_ID";

    public static final String EVENT_MATCH_FRIEND_ID = "EVENT_MATCH_FRIEND_ID";

    public static final String EVENT_RANDOM_MATCH_ID = "EVENT_RANDOM_MATCH_ID";

    public static final String EVENT_HANDCARD_ID = "EVENT_HANDCARD_ID";

    public static final String EVENT_TAKE_CARD_ID = "EVENT_TAKE_CARD_ID";

    public static final String EVENT_TAKE_OUT_CARD_ID = "EVENT_TAKE_OUT_CARD_ID";

    /**
     * 碰
     */
    public static final String EVENT_PONG_ID = "EVENT_PONG_ID";


    /**
     * 巴杠
     */
    public static final String EVENT_GANG_ID = "EVENT_GANG_ID";

    /**
     * 自摸
     */
    public static final String EVENT_HU_ID = "EVENT_HU_ID";

    /**
     * 过
     */
    public static final String EVENT_IGNORE_ID = "EVENT_IGNORE_ID";


    /**
     * 结束事件
     */
    public static final String EVENT_OVER_ID  = "EVENT_OVER_ID";



    /**
     * 接受到其他玩家出牌
     */
    public static final String EVENT_RECEIVE_OTHER_CARD_ID = "EVENT_RECEIVE_OTHER_CARD_ID";


    /**
     * 用户对事件的响应，比如pong，gang, hu
     */
    public static final String EVENT_RECEIVE_EVENT_REPLY_ID = "EVENT_RECEIVE_EVENT_REPLY_ID";



    /**
     * 客户端预请求执行游戏事件
     */
    public static final String EVENT_IS_GAME_EVENT_ID = "EVENT_IS_GAME_EVENT_ID";



    /**
     * 三种游戏事件临时状态
     */
    public static final String EVENT_STATUS_WAIT = "WAIT";

    public static final String EVENT_STATUS_CANCEL = "CANCEL";

    public static final String EVENT_STATUS_ACTION = "ACTION";

    /**
     * 设置缓存前缀
     */

    public static final String USER_MACHINE_PREV = "user-machine-id:";

    public static final String ROOM_INFO_PREV = "room-info-id:";



    public static String preUserMachinekey(String id) {
        return USER_MACHINE_PREV + id;
    }

    public static String preRoomInfoPrev(String id) {
        return ROOM_INFO_PREV + id;
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

     public static final String BASE_URL = "http://localhost:9002/";
    // public static final String BASE_URL = "http://120.26.198.241:9001/";

    public static final String REMOTE_LOGIN_URL = "http://localhost:9999/";

    public static final String REMOTE_USER_URL = "http://localhost:8081/";


}
