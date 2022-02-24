package ds.guang.majing.common.game.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ds.guang.majing.common.game.card.*;
import ds.guang.majing.common.util.Algorithm;
import ds.guang.majing.common.util.Converter;
import ds.guang.majing.common.game.dto.GameUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ds.guang.majing.common.util.DsConstant.EVENT_RECEIVE_OTHER_CARD_ID;
import static ds.guang.majing.common.util.DsConstant.EVENT_TAKE_CARD_ID;

/**
 * 玩家抽象接口
 *
 * @author guangyong.deng
 * @date 2021-12-17 14:05
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerPlayer.class, name = "serverPlayer")
})
@Getter
@Setter
@Accessors(chain = true)
public abstract class Player implements Cloneable, Serializable {

    protected GameUser gameUser;

    /**
     * 玩家手牌：在初始化时，会涉及多线程操作
     */
    @JsonSerialize(converter = Converter.class)
    protected volatile List<Integer> cards;

    /**
     * 事件手牌，包括 Gang 牌，PONG 牌
     */
    @JsonIgnore
    protected Map<Card, Integer> eventCard;


    /**
     * 预留字段：提示胡牌值
     */
    @JsonIgnore
    protected Set<Card> selectHu;


    /**
     * 最终胡牌值
     */
    @JsonIgnore
    protected Card selectedHu;


    /**
     * 胡牌
     */
    protected int stateHu;

    /**
     * 是否胡牌
     */
    protected boolean isHu;

    /**
     *
     * 1 - 北， 2 - 西，  3 - 南， 4 - 东
     * 方位
     */
    public int direction;

    /**
     * 游戏状态
     */
    protected GameState gameState;

    public Player() {}

    public Player(GameUser gameUser) {
        this.gameUser = gameUser;
        // 最多七个对子
        this.eventCard = new HashMap<>(8);
    }



    /**
     *
     * 加入手牌
     *
     * @param cardNum 手牌
     */
    public void addCard(int cardNum) {

        // 这里插入，必须保证手牌的有序性
        int index = Algorithm.binarySearch(cards, cardNum, true);
        if(index < 0) {
            throw new IllegalArgumentException("插入算法出现问题！");
        }
        // 因为 ArrayList.set 会发生边界检查，所以不能插入到右边界之外
        cards.add(index, cardNum);
    }


    /**
     *
     * 移除下标为 cardIndex 的手牌
     *
     * @param cardIndex 索引
     * @return 删除成功
     */
    public boolean removeCard(int cardIndex) {
        return false;
    }

    /**
     *
     * 移除点数为 cardNum 的手牌
     *
     * @param cardNum 手牌
     * @return 删除成功
     */
    public boolean remove(int cardNum) {
        // 移除一个元素
        return cards.remove((Integer)cardNum);
    }

    public void remove(int cardNum, int count) {
        for (int i = 0; i < count; i++) {
            if(!remove(cardNum)) {
                throw new IllegalArgumentException("移除失败，移除的次数" + (i + 1));
            }
        }
    }


    /**
     *
     * 检查玩家是否能出这张牌
     *
     * @param cardNum 这张牌是否在手牌中
     */
    public void checkOut(Integer cardNum) {
        if(cards != null && cards.contains(cardNum)) {
            return;
        }
        throw new IllegalArgumentException("错误的出牌-这张牌不存在手牌中！");
    }


    AtomicInteger hu = new AtomicInteger(0);
    /**
     *
     * 1.摸牌阶段，会先将 value 添加到手牌中，再做判断
     * 2.其他玩家出牌，则可以直接判断
     *
     *
     * @param card 引发此次事件的值
     * @param event 客户端事件
     */
    public GameEvent event(Card card, String event, String id) {

        Objects.requireNonNull(card, "card is null");

        Integer value = card.value();

         // 玩家事件集合
         Map<MaJiangEvent, Integer> selectEvent = new HashMap<>(16);

        // 1.是否可以 PONG
        if(EVENT_RECEIVE_OTHER_CARD_ID.equals(event) && Algorithm.sortCountArr(cards, value) == 2) {
            selectEvent.put(MaJiangEvent.PONG, value);
        }

        // 2.其他玩家出牌自身判断是否可以 GANG
        if(EVENT_RECEIVE_OTHER_CARD_ID.equals(event) && Algorithm.sortCountArr(cards, value) == 1) {
            selectEvent.put(MaJiangEvent.DIRECT_GANG, value);
        }

        // 4. 摸牌阶段判断是否可以暗杠
        if(EVENT_TAKE_CARD_ID.equals(event)) {

            List<Integer> four = Algorithm.sortCountFour(cards);

            if(!four.isEmpty()) {
                four.forEach(e -> selectEvent.put(MaJiangEvent.SELF_GANG, e));
            }
        }

        // 5.摸牌阶段是否可以巴杠
        if(EVENT_TAKE_CARD_ID.equals(event) && eventCard.containsKey(card)) {

            Integer count = eventCard.get(card);
            if(count == 3) {
                selectEvent.put(MaJiangEvent.IN_DIRECT_GANG, value);
            }else {
                throw new UnsupportedOperationException("棋牌错误！");
            }
        }

        // 6.摸牌阶段，判断是否可以自摸
        if(EVENT_TAKE_CARD_ID.equals(event) && Algorithm.isHu(cards)) {
            selectEvent.put( MaJiangEvent.SELF_HU, value);
        }

        // 其他玩家出牌自身判断是否可以 胡
        if(EVENT_RECEIVE_OTHER_CARD_ID.equals(event)) {

            List<Integer> copy = new ArrayList<>(cards);

            int i = Algorithm.binarySearch(copy, value, true);

            copy.add(i, value);
            // TODO: 胡牌算法还有些问题，后面修改
            if(Algorithm.isHu(null) || hu.incrementAndGet() == 5) {
                selectEvent.put(MaJiangEvent.IN_DIRECT_HU, value);
            }
        }

        if(selectEvent.isEmpty()) {
            return null;
        }
        // 加上默认忽略值
        selectEvent.put(MaJiangEvent.NOTHING, value);

        return new MaGameEvent()
                                .setSelectEvent(Collections.unmodifiableMap(selectEvent))
                                .setPlayId(id);
    }

    /**
     * 玩家 id
     * @return id
     */
    public String id() {
        return gameUser.getUserId();
    }


    /**
     * 返回 网络通道
     * @return 通道
     */
    public abstract Object getContext();




    /**
     *
     * 对游戏事件进行处理
     *
     * @param eventValue 事件 id
     * @param cardNum 棋牌值
     */
    public abstract void eventHandler(int eventValue, int cardNum);


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(gameUser, player.gameUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameUser);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"gameUser\":").append(gameUser)
                .append(", \"cards\":").append(cards)
                .append('}');
        return sb.toString();
    }
}
