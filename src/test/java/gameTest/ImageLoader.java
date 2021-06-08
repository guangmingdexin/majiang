package gameTest;

import com.guang.majiang.common.ImageRoot;
import com.guang.majiangclient.client.util.ConfigOperation;
import com.guang.majiangclient.client.util.ImageUtil;
import javafx.scene.image.ImageView;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;

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
        String path = ConfigOperation.config.get("images").toString();
        String dest = ConfigOperation.numToStr(18);
        System.out.println("path: " + path);
        System.out.println("dest: " + dest);
        ImageView image = ImageUtil.load(path, dest);

        System.out.println("image: " + image);
    }
}
