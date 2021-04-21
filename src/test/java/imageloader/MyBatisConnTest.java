package imageloader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @ClassName MyBatisConnTest
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/20 16:33
 * @Version 1.0
 **/
public class MyBatisConnTest {

    public static void main(String[] args) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");

        String classpath = resource.getPath();

        System.out.println(classpath);

        classpath = classpath.replace("test-classes/", "classes/config/mybatis/xml/mybatis-config.xml");

        try {

            InputStream inputStream = Resources.getResourceAsStream(classpath);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
