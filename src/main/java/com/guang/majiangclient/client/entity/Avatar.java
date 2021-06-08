package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName Avatar
 * @Description
 * @Author guangmingdexin
 * @Date 2021/3/23 9:15
 * @Version 1.0
 **/

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Avatar {

    private long avatarId;

    private String path;

    private String imageName;

    private String fileType;

    private long fileSize;

    // 该字段无法被反序列化
    @JsonIgnore
    private ImageView image;

    @JsonIgnore
    private ImageView imageLeft;

    @JsonIgnore
    private ImageView imageRight;

    @JsonIgnore
    private ImageView imageTop;

    private byte[] binaryImage;

    private String base64;

    // 玩家得分
    @JsonIgnore
    private int score;

    public Avatar(String path, String imageName, String fileType, long fileSize) {
        this.path = path;
        this.imageName = imageName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public Avatar(String base64, byte[] binaryImage, ImageView image, ImageView left,
                  ImageView right, ImageView top, int score) {

        this.binaryImage = binaryImage;
        this.base64 = base64;
        this.score = score;
        this.image = image;
        // 顺时针旋转 90 度
        this.imageLeft = left;

        // 顺时针 180 度
        this.imageRight = right;
        // 顺时针 270 度
        this.imageTop = top;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "avatarId=" + avatarId +
                ", path='" + path + '\'' +
                ", base64=" + ((base64 == null) ? "" : base64.substring(0, 10)) +
                '}';
    }
}
