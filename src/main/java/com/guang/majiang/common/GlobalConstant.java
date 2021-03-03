package com.guang.majiang.common;

import com.guang.majiang.image.CardImage;
import com.guang.majiang.image.MyImage;
import com.guang.majiang.layout.SimpleInit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName GlobalConstant
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/2/1 20:04
 * @Version 1.0
 **/
@Setter
@Getter
public class GlobalConstant {


    public static final int BG_HEIGHT;

    public static final int BG_WEITH;

    public static final int CARD_HEIGHT;

    public static final int CARD_WIDTH;

    public static final int FACE_DONW_HEIGHT;

    public static final int FACE_DOWN_LEFT_HEIGHT;

    public static final int MAX_TOTAL_WIDTH;

    public static final int MAX_TOTAL_HIGHT;

    public static final int MIDDLEX;

    public static final int MIDDLEY;

    static volatile GlobalConstant INSTANCE;

    static {
        SimpleInit init = new SimpleInit();

        // 1.加载背景图片
        MyImage bg = init.getBg();

        List<CardImage> cards = init.getCards();

        BG_HEIGHT = (int) bg.getImageView().getFitHeight();
        BG_WEITH = (int)bg.getImageView().getFitWidth();
        CARD_HEIGHT = (int)cards.get(0).getHeight();
        CARD_WIDTH = (int)cards.get(0).getWidth();
        FACE_DONW_HEIGHT = (int)cards.get(0).getFaceDownImage().getHeight();
        FACE_DOWN_LEFT_HEIGHT = (int)cards.get(0).getFaceDownImage().getFaceDownLeft().getFitHeight();
        MAX_TOTAL_WIDTH = 15 * CARD_WIDTH;
        MAX_TOTAL_HIGHT = 15 * FACE_DONW_HEIGHT;

        MIDDLEX = (GlobalConstant.BG_WEITH - GlobalConstant.CARD_WIDTH) / 2;
        MIDDLEY = (GlobalConstant.BG_HEIGHT - GlobalConstant.CARD_HEIGHT) / 2;
    }

    private GlobalConstant() {


    }

    public static GlobalConstant getInstance() {

        //
        if(INSTANCE == null) {

            synchronized (INSTANCE) {
                if(INSTANCE == null) {
                    return new GlobalConstant();
                }
            }
        }

        return INSTANCE;

    }

}
