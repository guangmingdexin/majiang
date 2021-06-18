package com.guang.majiangclient.client.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.ibatis.io.Resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @ClassName ImageUtil
 * @Author guangmingdexin
 * @Date 2021/4/24 10:05
 * @Version 1.0
 **/
public final class ImageUtil {


    /**
     *
     * 将图片转换为二进制数组
     * @param path 图片路径
     * @return 字节数组
     */
    public static byte[] imageToByte(String path) {
        try {
            // FileInputStream 的 available()就是返回的的，实际可读字节数，也就是总大小
            InputStream in = new FileInputStream(path);
            return convertToByte(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] convertToByte(InputStream in) {
        BufferedInputStream buf = new BufferedInputStream(in);
        byte[] imageData = new byte[0];
        try {
            imageData = new byte[in.available()];
            int i = 0;
            int read;
            while ((read = buf.read()) != -1) {
                // 判断是否需要扩容
                imageData[i++] = (byte)read;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageData;
    }

    /**
     * @param path 头像路径
     * @param recursive 是否为绝对路径
     * @return base64 编码
     */
    public static String encoderBase64(String path, boolean recursive) {
        byte[] bytes = new byte[0];
        InputStream stream;
        try {
            stream = (recursive) ? new FileInputStream(path) : Resources.getResourceAsStream(path) ;
            bytes = new byte[stream.available()];
            int i = stream.read(bytes);
            System.out.println("i : " + i);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decoderBase64(String s) {
        return Base64.getDecoder().decode(s);
    }

    public static ImageView load(File file, String dest) {
        if (file == null || dest == null) {
            return null;
        }

        String fileName = file.getName();

        if (file.isDirectory()) {
            // 递归加载
            File[] files = file.listFiles();

            if (files != null && files.length > 0) {
                for (File f : files) {
                    if(f.getName().equals(dest)) {
                        return load(f, dest);
                    }
                }
            }

        } else if (file.isFile() && fileName.equals(dest) && (fileName.endsWith(".png") || fileName.endsWith(".jpg"))) {
            ImageView view = new ImageView();
            Image image = null;
            try {
                image = new Image(fileToURL(file));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            view.setImage(image);
            view.setId(String.valueOf(System.identityHashCode(image)));
            view.setFitHeight(image.getHeight());
            view.setFitWidth(image.getWidth());
            return view;
        }

        return null;
    }



    public static ImageView load(String src, String dest) {
        return load(new File(src), dest);
    }

    public static ImageView load(String src, String dest, boolean isRelative) {
        if(isRelative) {
            // 使用 Resource 资源进行加载
            File file = null;
            try {
                file = Resources.getResourceAsFile(src);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return load(file, dest);
        }else {
            return load(src, dest);
        }
    }

    /**
     * @param file 源目录 文件名称
     * @param dest 图片名称
     * @return ImageView 对象
     */
    public static List<ImageView> load(File file, List<String> dest) {
        List<ImageView> imageViews = new ArrayList<>();
        for (String s : dest) {
            imageViews.add(load(file, s));
        }

        return imageViews;
    }

    public static List<ImageView> loads(String path) {
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
                        ImageView view = new ImageView();
                        Image image = new Image(fileToURL(f));
                        view.setImage(image);
                        view.setId(String.valueOf(System.identityHashCode(image)));
                        list.add(view);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    /**
     * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片，这是一种非常简单的方式。
     * @param imageFile
     * @return
     */
    public static boolean checkImage(File imageFile) {
        if (!imageFile.exists()) {
            return false;
        }
        if(imageFile.length() > 2 * 1024 * 1024) {
            return false;
        }

        BufferedImage img;
        try {
            img = ImageIO.read(imageFile);
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            return false;
        } finally {
            // help GC
            img = null;
        }
    }

    public static ImageView bytesConvertToImage(byte[] bytes, int width, int height, int rotate) {

        Image image = new Image(new ByteArrayInputStream(bytes));
        ImageView view = new ImageView(image);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setRotate(rotate);
        return view;
    }

    public static void main(String[] args) {
        ImageView load = load("rotate_image/", "bamboo1-left.png", true);
        System.out.println(load);
    }

    static String fileToURL(File file) throws MalformedURLException {

        return file.toURI().toURL().toString();

    }

    public static void translateImage(ImageView view, double size) {
        view.setFitHeight(view.getFitWidth() * size);
        view.setFitWidth(view.getFitWidth() * size);
    }



}
