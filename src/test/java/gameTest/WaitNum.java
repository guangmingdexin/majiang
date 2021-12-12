package gameTest;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        BigDecimal bigDecimal = new BigDecimal("0.1");

        bigDecimal.add(new BigDecimal("0.3"));

    }
}
