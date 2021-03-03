package com.guang.majiang.layout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LoadImpl
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/13 16:20
 * @Version 1.0
 **/
public class LoadImpl implements Load {

    @Override
    public ImageView load(String src) {

        File file = new File(src);

        try {
            if(file.exists()) {

                String fileName = file.getName();

                if(file.isDirectory()) {
                    // 获取下面的文件名，从中选择一张加载并返回
                    System.out.println(src);
                    return loads(src).get(0);
                }

                if(file.isFile() && (fileName.endsWith(".png") || fileName.endsWith(".jpg"))) {
                    ImageView view = new ImageView();

                    Image image = new Image(Load.fileToURL(file));
                    view.setImage(image);
                    view.setId(fileName);
                    view.setFitHeight(image.getHeight());
                    view.setFitWidth(image.getWidth());
                    return view;
                }

            }else {
                System.out.println("文件不存在！");
                return null;
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ImageView> loads(String path) {

        File file = new File(path);

        List<ImageView> list = new ArrayList<>();

        if(file.isDirectory()) {

            File[] files = file.listFiles();

            assert files != null;
            for (File f : files) {

                String fileName = f.getName();

                boolean flag = fileName.endsWith(".png") || fileName.endsWith(".jpg");

                if(f.isFile() && flag) {
                    try {
                        // 每张图片重复 4 次

                        for (int i = 1; i <= 4; i++) {
                            ImageView view = new ImageView();
                            view.setImage(new Image(Load.fileToURL(f)));
                            view.setId(fileName);
                            list.add(view);
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return list;
    }


}
