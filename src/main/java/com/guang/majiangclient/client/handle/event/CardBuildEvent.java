package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.CardImage;
import com.guang.majiangclient.client.entity.GameInfoCard;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.util.CommonUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName CardBuildEvent
 * @Author guangmingdexin
 * @Date 2021/5/29 15:14
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
public class CardBuildEvent implements Runnable{

    private Direction cur;

    private HashMap<Direction, GameInfoCard> infoCards;

    public CardBuildEvent(Direction cur, HashMap<Direction, GameInfoCard> infoCards) {
        this.cur = cur;
        this.infoCards = infoCards;
    }

    @Override
    public void run() {

        ClientLayout.gBottom.setAlignment(Pos.CENTER);
        ClientLayout.gTop.setAlignment(Pos.CENTER);
        ClientLayout.gLeft.setAlignment(Pos.CENTER);
        ClientLayout.gRight.setAlignment(Pos.CENTER);

        Direction rightDir = Direction.valueOf((cur.getDirection() + 1) % 4);
        Direction topDir = Direction.valueOf((cur.getDirection() + 2) % 4);
        Direction leftDir = Direction.valueOf((cur.getDirection() + 3) % 4);

        List<CardImage> cur = infoCards.get(this.cur).getCardImages();
        List<CardImage> left = infoCards.get(leftDir).getCardImages();
        List<CardImage> right = infoCards.get(rightDir).getCardImages();
        List<CardImage> top = infoCards.get(topDir).getCardImages();

        for (int i = 0; i < cur.size(); i++) {
            ClientLayout.gBottomGd.add(cur.get(i).getCard(), i, 0);
        }

        for (int i = 0; i < top.size(); i++) {
            ClientLayout.gTop.add(top.get(i).getFaceDownPositive(), i, 0);
        }

        for (int i = 0; i < left.size(); i++) {
            ClientLayout.gLeft.add(left.get(i).getFaceDownLeft(), 0, i);
        }

        for (int i = 0; i < right.size(); i++) {
            ClientLayout.gRight.add(right.get(i).getFaceDownRight(), 0, i);
        }

        System.out.println("room-cur： " + CacheUtil.getCurDire());


        ClientLayout.bottomMarker = new Label("出牌");
        ClientLayout.hBottom.getChildren().add(ClientLayout.bottomMarker);
        AnchorPane.setLeftAnchor(ClientLayout.bottomMarker, 280d);

        CommonUtil.markerUi(CacheUtil.getCurDire());
    }
}
