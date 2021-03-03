package com.guang.majiang.event;

import com.guang.majiang.ai.AITask;
import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.Direction;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.layout.UIInit;
import com.guang.majiang.lock.MoreCondition;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName CardHandler
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/2 19:07
 * @Version 1.0
 **/

public class CardHandler implements EventHandler<MouseEvent> {

    private OperationFunc func;

    private List<CardImage> cards;

    private CardImage  c;

    private Player player;

    private  PlayerCard playerCard;

    private  PlayerCard[] playerCards;

    public CardHandler(OperationFunc func, CardImage c,  PlayerCard playerCard) {
        this.func = func;
        this.c = c;
        this.player = playerCard.getPlayer();
        this.playerCard = playerCard;
        this.cards = playerCard.getCards();
        this.playerCards = playerCard.getPlayerCards();
    }

    @Override
    public void handle(MouseEvent event) {

        // 首先判断当前状态
        if (c.getCardStatus() == CardStatus.STORAGE) {
            System.out.println("点击了一张无效的牌！");
            // 此时无法点击
        } else if (c.getCardStatus() == CardStatus.HOLD) {
            // 首先判断是否还有其他牌的状态也为 READY
            for (CardImage card : cards) {
                if (card.getCardStatus() == CardStatus.READY &&
                        !card.equals(c)) {
                    // 如果有 将该牌 取消 READY 状态
                    func.readyDown(card.getImageView());
                    card.setCardStatus(CardStatus.HOLD);
                }
            }
            // 牌已经在玩家手中，此时点击鼠标 进入下一个状态
            c.setCardStatus(CardStatus.READY);
            func.readyOn(c.getImageView());

        } else if (c.getCardStatus() == CardStatus.READY) {

            // 首先判断是否为自己的回合
            if(player.getIsRound() == 0) {
                System.out.println(player.getName() + "不是自己的回合，不能出牌！");
                return;
            }

            // 获取当前牌的索引
            int target = cards.indexOf(c);
            System.out.println(player.getName() + "是自己的回合可以出牌" + "-> " + target);
            // 出牌
            cards.remove(c);
            // usedCards.add(c);

            // 变换 x, y
            // 获取方位
            Direction direction = player.getDirection();

            // 如果方位为下
            if (direction == Direction.UNDER) {

                // 表明是自己出牌
                // 计算出放牌的起始位置和结束位置
                int startX = GlobalConstant.MIDDLEX - 5 * GlobalConstant.CARD_WIDTH;
                int startY = GlobalConstant.MIDDLEY + 2 * GlobalConstant.CARD_HEIGHT;

                // 计算 应该摆放位置
                int curX = startX + (playerCard.getUsedCards().size() % 10) * GlobalConstant.CARD_WIDTH;

                int curY = startY + (playerCard.getUsedCards().size() / 10) * GlobalConstant.CARD_HEIGHT;

                c.getImageView().setX(curX);
                c.getImageView().setY(curY);

                c.setCardStatus(CardStatus.OUT);
                playerCard.getUsedCards().add(c);

                // 出牌之后，需要其他玩家进行判断是否需要 碰 杠 胡
                // 触发判定事件
                // 下家的出牌标志位变为 1 自己变为 0
                // 整理手牌
                for (PlayerCard p : playerCards) {
                    p.setOldCard(c);
                }

                UIInit.clearHandCard(player, cards, c, target);
            }

            System.out.println("打出牌！");

            ReentrantLock lock = MoreCondition.lock;

            Condition waitAiRound = MoreCondition.waitAiRound;

            lock.lock();

            try {
                AITask.setIsRound(player, playerCards);

                waitAiRound.signal();
            }finally {
                lock.unlock();
            }

        }

    }



}
