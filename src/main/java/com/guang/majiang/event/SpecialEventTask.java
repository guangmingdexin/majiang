package com.guang.majiang.event;

import com.guang.majiang.common.SpecialEvent;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.player.Player;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * @ClassName SpecialEventTask
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/13 16:17
 * @Version 1.0
 **/
public class SpecialEventTask implements Runnable {

    private int res;

    protected List<ImageView> eventImages;

    protected CardImage takeOut;

    protected List<CardImage> handCards;

    protected List<CardImage[]> bump;

    protected volatile boolean over;

    protected Player player;

    protected volatile SpecialEvent event;

    public SpecialEventTask(int res, List<ImageView> eventImages, CardImage takeOut,
                            List<CardImage> handCards, List<CardImage[]> bump,
                            Player player) {
        this.res = res;
        this.eventImages = eventImages;
        this.takeOut = takeOut;
        this.handCards = handCards;
        this.bump = bump;
        this.player = player;
    }

    @Override
    public void run() {
        // 碰 杠 胡
        for (ImageView img : eventImages) {
            img.setVisible(true);
            if("ignore.png".equals(img.getId())) {
                img.setOnMouseClicked(new IgnoreHandler(this));
                continue;
            }
            if ((res & 1) == 0) {
                img.setStyle("-fx-opacity: 0.5;");
                img.setOnMouseClicked(null);
            }else if("pong.png".equals(img.getId())) {
                System.out.println("碰");
                img.setStyle("-fx-opacity: 1;");
                img.setOnMouseClicked(new PongHandler(this));
            }else if("kong.png".equals(img.getId())) {
                System.out.println("杠！");
                img.setStyle("-fx-opacity: 1;");
                img.setOnMouseClicked(new KongHandler(this));
            }
            res >>>= 1;
        }

    }

    public boolean isOver() {
        return over;
    }

    public SpecialEvent getEvent() {
        return event;
    }
}
