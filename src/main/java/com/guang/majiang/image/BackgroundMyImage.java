package com.guang.majiang.image;

import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName BackgroundImage
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/12 21:45
 * @Version 1.0
 **/
@Getter
@Setter
public class BackgroundMyImage extends MyImage {

    private double height;

    private double width;

    public BackgroundMyImage(String id, String src, String name, ImageView imageView) {
        super(id, src, name, imageView);
    }

    public BackgroundMyImage(String id, String src, String name, ImageView imageView, double height, double width) {
        super(id, src, name, imageView);
        this.height = height;
        this.width = width;
    }
}
