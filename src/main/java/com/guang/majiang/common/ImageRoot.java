package com.guang.majiang.common;

import java.io.File;

/**
 * @author guangmingdexin
 */

public enum ImageRoot {


     MAJIANG_IMAGE_ROOT("image"),

     BACKGROUNG_IMAGE_ROOT("background");


     private final String root = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";

     private String path;

    ImageRoot(String path) {
        this.path = root  + path;
    }

    public String getPath() {
        return path;
    }
}
