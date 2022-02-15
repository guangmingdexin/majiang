package ds.guang.majing.common.game.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.CardType;
import ds.guang.majing.common.game.card.MaJiang;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.dto.GameUser;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

import static ds.guang.majing.common.game.card.MaJiangEvent.*;

/**
 *
 * @author asus
 */
public class ServerPlayer extends Player  {

    @JsonIgnore
    private transient ChannelHandlerContext context;

    public ServerPlayer() {
    }

    public ServerPlayer(GameUser gameUser) {
        super(gameUser);
    }

    public ServerPlayer setContext(ChannelHandlerContext context) {
        this.context = context;
        return this;
    }


    @Override
    public Object getContext() {
        return context;
    }

    @Override
    public Player convertTo() {
        // 对象复制
        return new ClientPlayer()
                .setGameUser(getGameUser())
                .setCards(getCards());
    }

    @Override
    public void eventHandler(int eventValue, int cardNum) {

        Card card = new MaJiang(cardNum, CardType.generate(cardNum));

        // 此时可能第一次发生事件即 eventValue 为 null
        if(eventCard == null) {
            eventCard = new HashMap<>(8);
        }

        if(eventValue == PONG.getValue()) {
            // pong 事件 移除玩家手牌到 事件手牌区，同时将桌面手牌同样移除
            // 测试阶段：只要有一个牌一样就可以pong
            // remove(cardNum, 1);
            remove(cardNum, 2);
            checkNotExist(card);
            eventCard.put(card, 3);

        }else if(eventValue == DIRECT_GANG.getValue()) {
            // 直杠
             remove(cardNum, 1);
            // remove(cardNum, 3);
            checkNotExist(card);
            eventCard.put(card, 4);
        }else if(eventValue == IN_DIRECT_GANG.getValue()) {
            // 巴杠
            checkExist(card);
            remove(cardNum);
            eventCard.put(card, 4);

        } else if(eventValue == SELF_GANG.getValue()) {
            // 暗杠
            checkExist(card);
            remove(cardNum, 4);
            eventCard.put(card, 4);
        } else if(eventValue == IN_DIRECT_HU.getValue() || eventValue == SELF_HU.getValue()) {

            // 胡
            // 设置玩家状态，设置房间状态
            setSelectedHu(card);
            setStateHu(eventValue);
            setHu(true);
        }
    }

    private void checkNotExist(Card card) {

         if(eventCard != null && eventCard.containsKey(card)) {
            throw new IllegalArgumentException("已经存在");
         }
    }


    private void checkExist(Card card) {
        if(eventCard == null || (!eventCard.containsKey(card))) {
            throw new IllegalArgumentException("不存在碰牌");
        }
    }
}
