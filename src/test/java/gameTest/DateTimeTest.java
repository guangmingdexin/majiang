package gameTest;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName DateTimeTest
 * @Author guangmingdexin
 * @Date 2021/6/17 9:27
 * @Version 1.0
 **/
public class DateTimeTest {

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(new Date());
    }
}
