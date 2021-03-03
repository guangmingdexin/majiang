package imageloader;

import com.guang.majiang.common.ImageRoot;

import java.io.File;
import java.util.Arrays;

/**
 * @ClassName ImageLoader
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/1/12 21:53
 * @Version 1.0
 **/
public class ImageLoader {

    static final String imagePath = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";

    private static void imageLoader() {

        System.out.println(System.getProperty("user.dir"));
    }

    private static String enumTest() {

        return ImageRoot.BACKGROUNG_IMAGE_ROOT.getPath();
    }

    public static void main(String[] args) {
//        System.out.println(new File(imagePath).exists());
//        System.out.println(imagePath);
//        imageLoader();

        System.out.println(enumTest());
    }
}
