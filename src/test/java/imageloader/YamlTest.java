package imageloader;

import com.guang.majiangserver.config.ConfigOperation;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @ClassName YamlTest
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/12 19:53
 * @Version 1.0
 **/
public class YamlTest {

    public static void main(String[] args) {
        Yaml yaml = new Yaml();

        try {
            Map load = yaml.load(new FileInputStream(ConfigOperation.PATH));

            System.out.println(load.get("avatar"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
