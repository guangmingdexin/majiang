package com.guang.majiang.image;

import javafx.scene.image.ImageView;
import lombok.*;

/**
 * @ClassName Image
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/12 11:27
 * @Version 1.0
 **/
@Getter
@Setter
public abstract class MyImage {

    private String id;

    private String src;

    private String name;

    private ImageView imageView;

    public MyImage(String id, String src, String name, ImageView imageView) {
        this.id = id;
        this.src = src;
        this.name = name;
        this.imageView = imageView;
    }
}
