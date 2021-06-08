package gameTest;

import com.guang.majiangclient.client.common.MessageFactory;
import com.guang.majiangclient.client.common.annotation.Package;

import java.util.Set;

/**
 * @ClassName ClassUtil
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/13 8:32
 * @Version 1.0
 **/
public class ClassUtil {

    public static void main(String[] args) {
        Set<Class<?>> classes = com.guang.majiangclient.client.util.ClassUtil.getClassFromPath("com.guang.majiangclient.client.message",
                Package.class, true);

//        for (Class<?> aClass : classes) {
//            System.out.println(aClass.getName());
//        }

        MessageFactory.registerAll(classes);

        Class<?> aClass = MessageFactory.getClass((short) 16, (short) 99);

        System.out.println(aClass.getName());
    }
}
