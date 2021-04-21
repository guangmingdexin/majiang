package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName Avatar
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/23 9:15
 * @Version 1.0
 **/

@Setter
@Getter
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class Avatar {

    private String path;

    private String imageName;

    private String fileType;

    private int fileSize;

    // 该字段无法被反序列化
    @JsonIgnore
    private ImageView image;

    public Avatar() {
    }



}
