package com.guang.majiang.common;

import java.io.File;

/**
 * @author guangmingdexin
 */

public enum ImageRoot {


     MAJIANG_IMAGE_ROOT("image"),

     BACKGROUNG_IMAGE_ROOT("background"),

     EVENT_IMAGE_ROOT("event"),

     ARROW_IMAGE_ROOT("arrow"),

     GAME_CLIENT_CONFIG("config");

     private final String root = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";

     private String path;

    ImageRoot(String path) {
        this.path = root  + path;
    }

    public String getPath() {
        return path;
    }
}
