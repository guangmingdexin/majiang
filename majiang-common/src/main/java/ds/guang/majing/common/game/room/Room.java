package ds.guang.majing.common.game.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majing.common.game.machines.StateMachines;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.StateMachine;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

import static ds.guang.majing.common.util.DsConstant.preRoomInfoPrev;
import static ds.guang.majing.common.util.DsConstant.preUserMachinekey;

/**
 * @author asus
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerFourRoom.class, name = "serverRoom"),
        @JsonSubTypes.Type(value = ClientFourRoom.class, name = "clientRoom")
})
@Getter
@Setter
public abstract class Room implements Serializable {

    /**
     * room Id
     */
    protected String id;

    /**
     * 玩家集合
     */
    protected Player[] players;

    /**
     * 当前使用的手牌下标
     */
    protected int markIndex;

    /**
     * 当前玩家回合下标（同位置相关，即该数字 % 4 可以对应到相应玩家，同时
     * 数组下标即为方位 比如 [A, B, C, D] -> [北，西，南，东]
     * ）
     */
    protected volatile int curRoundIndex;

    /**
     * 玩家人数
     */
    protected int playerCount;

    /**
     * 初始手牌数量
     */
    protected int initialCardNum;

    /**
     * 棋牌游戏中进行状态判断-最大手牌数量
     */
    protected int maxCardNum;

    /**
     * 最小手牌数量
     */
    protected int minCardNum;

    /**
     * 手牌池，所有手牌集合
     */
    @JsonIgnore
    private transient List<Integer> initialCards;

    /**
     * 游戏事件处理器
     */
    @JsonIgnore
    protected transient GameEventHandler eventHandler;

    public Player[] getPlayers() {
        return players;
    }

    public Room() {}

    /**
     *
     * 判断当前是否为当前玩家回合
     *
     * @param userId 用户 id
     * @return
     */
    public abstract boolean isCurAround(String userId);

    /**
     *
     * 对玩家手牌状态进行检查
     *
     * @param userId 用户 id
     * @return
     */
    public abstract boolean check(String userId);




    /**
     *
     * 根据用户 id 获取玩家信息
     *
     * @param userId
     * @return
     */
    public Player findPlayerById(String userId) {

        Objects.requireNonNull(userId, "userId must be not empty!");
        for (Player player : players) {
            if(userId.equals(player.id())) {
                return player;
            }
        }
        return null;
    }

    /**
     * 获取指定玩家的方位
     * @param userId 玩家 id
     * @return
     */
    public int direction(String userId) {

        for (int i = 0; i < players.length; i++) {

            if(players[i].id().equals(userId)) {

                return i;
            }
        }

        return -1;
    }


    public StateMachine findPlayerState(String userId) {
        return StateMachines.get(preUserMachinekey(userId));
    }


    public StateMachine nextPlayerState() {
        int next = getCurRoundIndex() % getPlayerCount();
        Player nextPlayer = players[next];
        return findPlayerState(nextPlayer.id());
    }


    /**
     *
     * 设置初始麻将
     */
    protected static final List<Integer> CARDS = initialCards();

    private static List<Integer> initialCards() {

        // 1.服务器生成 108 张棋牌（只可读）
        List<Integer> cards = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                for (int k = 1; k <= 4; k++) {
                    // 11 -- 万, 111 -- 条, 1111 -- 筒
                    cards.add((int) (j + Math.pow(10, i)));
                }
            }
        }

        return cards;
    }

    public  void assignCardToPlayer() {
        // 初始手牌
        int initialCardNum = this.initialCardNum;

        // 初始玩家人数
        int playerCount = this.playerCount;

        if(initialCards.size() < initialCardNum * playerCount
                || initialCardNum <= 0
                || players == null
                || players.length < playerCount) {
            throw new IllegalArgumentException("初始化错误！");
        }


        for (int j = 0; j < playerCount; j++) {
            List<Integer> cards = players[j].getCards();
            if(cards != null) {
                cards.clear();
                continue;
            }
            // TODO 具体使用 数组还是链表，有待考虑
            cards = new ArrayList<>();
            players[j].setCards(cards);
        }

        for (int i = 0; i < initialCardNum; i++) {
            for (int j = 0; j < playerCount; j++) {
                List<Integer> cards = players[j].getCards();
                // 从手牌池中取出一张手牌
                cards.add(initialCards.get(markIndex++));
            }
        }

        // 对每个玩家的手牌进行排序
        for (int i = 0; i < playerCount; i++) {
            Collections.sort(players[i].getCards());
        }

    }

    /**
     *
     * 洗牌算法
     * 随机生成一个 1-n 的随机数，从最后一个数组开始
     * 不断交换 card[random]，card[i]
     *
     * @param cards 初始手牌
     */
    protected List<Integer> shuffle(List<Integer> cards) {
        // TODO : 可以考虑使用 ThreadRandom 优化
        Random rand = new Random();

        List<Integer> copyCards = new ArrayList<>(cards);

        // 洗牌 1 - 107
        for (int i = copyCards.size() - 1; i >= 1 ; i--) {

            int randInd = rand.nextInt(i);
            // 交换
            swap(copyCards, i, randInd);
        }
        swap(copyCards, 0, copyCards.size() - 1);
        return Collections.unmodifiableList(copyCards);

    }


    private void swap(List<Integer> copyCards, int i, int randInd) {
        Integer temp = copyCards.get(i);
        copyCards.set(i, copyCards.get(randInd));
        copyCards.set(randInd, temp);
    }


    /**
     * @param id 玩家 id
     * @return
     */
    public static Room getRoomById(String id) {

        // 获取房间管理器
        RoomManager roomManager = RoomManager.getInstance();
        return roomManager.get(preRoomInfoPrev(id));
    }

  /**                封装的一些方法，减少代码量                   */

    public static void write(String id, Object message) {

        Room room = getRoomById(id);
        Player p = room.findPlayerById(id);
        ChannelHandlerContext context = (ChannelHandlerContext) p.getContext();

        context.channel().eventLoop().execute(() -> {
           context.writeAndFlush(message);
        });

    }

    /**
     *
     * @param id 调用者 id
     *
     */
    public static void announce(String id) {
        Room room = getRoomById(id);
        GameEventHandler eventHandler = room.getEventHandler();
        eventHandler.announce(id, false);
    }

    public static Room nextRound(String id, int eventValue) {
        Room room = getRoomById(id);
        GameEventHandler eventHandler = room.getEventHandler();
        eventHandler.nextRound(eventValue, id, room);
        return room;
    }


    public boolean remove(String id, int cardNum) {
        Player p = findPlayerById(id);
        return p.remove(cardNum);
    }

    public void announceNext() {
        eventHandler.announceNext();
    }

    public void eventHandler(String id, int eventValue, int cardNum) {
        Player p = findPlayerById(id);
        p.eventHandler(eventValue, cardNum);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"id\":").append(id)
                .append(", \"players\":").append(Arrays.toString(players))
                .append(", \"markIndex\":").append(markIndex)
                .append(", \"curRoundIndex\":").append(curRoundIndex)
                .append(", \"playerCount\":").append(playerCount)
                .append(", \"cards\":").append(initialCards)
                .append('}');
        return sb.toString();
    }
}
