package gameTest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName WaitNum
 * @Author guangmingdexin
 * @Date 2021/5/30 13:29
 * @Version 1.0
 **/
public class WaitNum {

    public static void main(String[] args) {

        AtomicInteger waitNum = new AtomicInteger(4);

        System.out.println(waitNum.decrementAndGet());
        System.out.println(waitNum.decrementAndGet());
        System.out.println(waitNum.decrementAndGet());
        System.out.println(waitNum.decrementAndGet());

    }
}
