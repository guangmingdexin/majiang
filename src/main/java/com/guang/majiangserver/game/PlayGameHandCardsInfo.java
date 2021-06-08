package com.guang.majiangserver.game;

import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.entity.Room;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName PlayInfo
 * @Author guangmingdexin
 * @Date 2021/5/21 16:01
 * @Version 1.0
 **/
@Getter
@Setter
public class PlayGameHandCardsInfo {

    private Room room;

    // 手牌
    private List<Integer> cards;

    // 庄家位置
    private Direction marker;

    public PlayGameHandCardsInfo(Room room) {
        this.room = room;
        this.cards = new LinkedList<>();
        initialCard();
        shuffle();
    }

    private void initialCard() {
        // 填充数字
        // 万
//        points.put('c', 10);
//        // 条
//        points.put('b', 100);
//        // 筒
//        points.put('d', 1000);
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                for (int k = 1; k <= 4; k++) {
                    cards.add((int) (j + Math.pow(10, i)));
                }
            }
        }
    }

    private void shuffle() {
        if(cards.size() > 108) {
            throw new IllegalArgumentException("牌数不对！");
        }
        Collections.shuffle(cards);
    }


}
