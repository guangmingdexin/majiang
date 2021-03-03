package com.guang.majiang.ai;

import com.guang.majiang.common.Direction;
import com.guang.majiang.layout.UIInit;
import com.guang.majiang.lock.MoreCondition;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerCard;
import javafx.application.Platform;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName AITask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/28 16:02
 * @Version 1.0
 **/
public class AITask extends Thread {

    private PlayerCard[] playerCards;

    public AITask(PlayerCard[] playerCards) {
        this.playerCards = playerCards;

    }

    @Override
    public void run() {
        super.run();

        // 1. 首先判断是否应该 ai 出牌，即当前 isRound 为  1 direction != under
        ReentrantLock lock = MoreCondition.lock;

        Condition waitAiRound = MoreCondition.waitAiRound;

        lock.lock();

        try {
            while (true) {
                if (isAIRound()) {

                    PlayerCard cur = getCurAi();
                    // 触发事件
                    // 随机出牌
                    assert cur != null;

                    Platform.runLater(() -> {
                        UIInit.aiHandCard(cur);
                    });

                    setIsRound(cur.getPlayer(), playerCards);

                }else {
                    // 获取其他玩家出的牌 判断是否需要 碰 杠 胡

                    System.out.println("等待...");
                    waitAiRound.await();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("有问题");
        } finally {
            lock.unlock();
        }


    }


    private boolean isAIRound() {

        Player player = getCurPlayer().getPlayer();

        if(player.getIsRound() == 0 && player.getDirection() == Direction.UNDER) {
            return true;
        }

        return false;
    }

    private PlayerCard getCurAi() {

        Player player = getCurPlayer().getPlayer();

        for (PlayerCard playerCard : playerCards) {

            Player p = playerCard.getPlayer();

            if(p != player && p.getIsRound() == 1) {
                return playerCard;
            }

        }

        return null;
    }

    private PlayerCard getCurPlayer() {

        for (PlayerCard playerCard : playerCards) {

            Player p = playerCard.getPlayer();

            if(p.getDirection() == Direction.UNDER) {
                return playerCard;
            }

        }

        return null;

    }

    public synchronized static void setIsRound(Player player, PlayerCard[] playerCards) {

        player.setIsRound(0);

        // 找到下家
        Direction direction = player.getDirection();

        Direction nextDirection = null;

        if(direction == Direction.UNDER) {
            nextDirection = Direction.RIGHT;
        }else if(direction == Direction.LEFT) {
            nextDirection = Direction.UNDER;
        }else if(direction == Direction.ABOVE) {
            nextDirection = Direction.LEFT;
        }else if(direction == Direction.RIGHT) {
            nextDirection = Direction.ABOVE;
        }

        for (PlayerCard playerCard : playerCards) {
            Player p = playerCard.getPlayer();
            if(p.getDirection() == nextDirection) {
                p.setIsRound(1);
                return;
            }
        }
    }
}
