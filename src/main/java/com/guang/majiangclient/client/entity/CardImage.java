package com.guang.majiangclient.client.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.ImageUtil;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @ClassName CardImage
 * @Author guangmingdexin
 * @Date 2021/5/22 16:25
 * @Version 1.0
 **/
@Getter
@Setter
@NoArgsConstructor
public class CardImage {

    @JsonIgnore
    private ImageView card;

    @JsonIgnore
    private ImageView cardLeft;

    @JsonIgnore
    private ImageView cardRight;

    @JsonIgnore
    private ImageView cardTop;

    @JsonIgnore
    private  ImageView faceDownPositive;

    @JsonIgnore
    private  ImageView faceDownLeft;

    @JsonIgnore
    private ImageView faceDownRight;

    private int value;



    /**
     * -1 未使用
     * 0 玩家手牌
     * 1 准备出牌
     * 2 已经出牌
     * 3 碰牌
     * 4 杠牌
     * 5 胡牌
     *
     */
    private int flag;

    public CardImage(int value, int flag) {
        this.value = value;
        this.flag = flag;
    }

    public CardImage build(int value, int flag, ImageView view) {
        this.value = value;
        this.flag = flag;
        this.card = view;
        return this;
    }

    public CardImage buildLeft(int value, int flag) {
        this.value = value;
        this.flag = flag;
        String name = ConfigOperation.numToStr(value).replace(".png", "") + "-left.png";
        this.cardLeft = ImageUtil.load(ConfigOperation.config.get("rotate_image").toString(),name, true);
        return this;
    }

    public CardImage buildRight(int value, int flag) {
        this.value = value;
        this.flag = flag;
        String name = ConfigOperation.numToStr(value).replace(".png", "") + "-right.png";
        this.cardRight = ImageUtil.load(ConfigOperation.config.get("rotate_image").toString(),name, true);
        return this;
    }

    public CardImage buildTop(int value, int flag) {
        this.value = value;
        this.flag = flag;
        String name = ConfigOperation.numToStr(value).replace(".png", "") + "-top.png";
        this.cardTop = ImageUtil.load(ConfigOperation.config.get("rotate_image").toString(), name, true);
        return this;
    }

    public CardImage buildDown() {

        String path = ConfigOperation.config.get("images").toString();
        faceDownPositive = ImageUtil.load(path, ConfigOperation.config.get("face-down").toString(), true);
        faceDownLeft = ImageUtil.load(path, ConfigOperation.config.get("face-down-left").toString(), true);
        faceDownRight = ImageUtil.load(path, ConfigOperation.config.get("face-down-right").toString(), true);

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj instanceof CardImage) {
            CardImage cardImage = (CardImage) obj;
            return card == cardImage.card && value == cardImage.value
                    && flag == cardImage.flag;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return card.hashCode();
    }

    @Override
    public String toString() {
        return "CardImage{" +
                "card=" + card +
                ", value=" + value +
                ", flag=" + flag +
                '}';
    }
}
