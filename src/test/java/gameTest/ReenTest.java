package gameTest;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName ReenTest
 * @Author guangmingdexin
 * @Date 2021/5/22 14:43
 * @Version 1.0
 **/
public class ReenTest {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock(true);

        lock.lock();

        try {

        }finally {
            lock.unlock();
        }
    }
}
