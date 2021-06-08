package com.guang.majiang.layout;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface Load {


    /**
     * 加载图片
     *
     * @param src 图片
     * @return Imageview 对象
     */
    ImageView load(String src);

    /**
     * 加载某一个目录下的所有图片
     *
     * @param path 图片目录
     * @return
     */
    List<ImageView> loads(String path);

    static String fileToURL(File file) throws MalformedURLException {

        return file.toURI().toURL().toString();

    }

}
