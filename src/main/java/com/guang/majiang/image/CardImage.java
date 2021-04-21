package com.guang.majiang.image;

import com.guang.majiang.common.CardStatus;
import com.guang.majiang.common.CardType;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName CardImage
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 18:30
 * @Version 1.0
 **/
@Getter
@Setter
public class CardImage extends MyImage implements Comparable<CardImage> {

    private double height;

    private double width;

    private CardType cardType;

    private int value;

    private CardStatus cardStatus;

    private CardFaceImage faceDownImage;

    public CardImage(String id, String src, String name, ImageView imageView,
                     double height, double width, CardType cardType, int value,
                     CardStatus cardStatus, CardFaceImage cardFaceImage) {
        super(id, src, name, imageView);
        this.height = height;
        this.width = width;
        this.cardType = cardType;
        this.value = value;
        this.cardStatus = cardStatus;
        this.faceDownImage = cardFaceImage;
    }


    @Override
    public int compareTo(CardImage o) {
        return this.getValue() - o.getValue();
    }
}
