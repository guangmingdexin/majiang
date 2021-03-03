package com.guang.majiang.image;

import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

/**
 * 麻将背面图片
 *
 * @ClassName CardFaceImage
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 20:08
 * @Version 1.0
 **/
@Getter
@Setter
public class CardFaceImage extends MyImage {

    private double height;

    private double width;

    private ImageView faceDownLeft;

    private ImageView faceDownRight;


    public CardFaceImage(String id, String src, String name, ImageView imageView, double height, double width) {
        super(id, src, name, imageView);
        this.height = height;
        this.width = width;
    }
}
