package ds.guang.majiang.server.layer.basic;

import java.util.Deque;
import java.util.concurrent.*;

public class ThreadDemo {


    public static void main(String[] args) {

        Deque<Integer> deque = new LinkedBlockingDeque<>(2);

        Future<String> future =
                Executors.newSingleThreadExecutor().submit(
                        () -> {

                            for (;;) {

                                if(deque.size() > 2) {

                                    while (!deque.isEmpty()) {
                                        deque.poll();
                                    }
                                    return "hello world!";
                                }else {

                                }
                            }

                        });


        Thread t1 = new Thread(() -> {

            System.out.println("t1 开始执行 future 任务！");
            try {
                deque.add(1);

                Thread.sleep(1000);
                System.out.println("添加1");
                String s = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("t1 结束future 任务！");
        }, "t1");

        Thread t2 = new Thread(() -> {

            System.out.println("t2 开始执行 future 任务！");
            try {

                deque.add(2);

                Thread.sleep(1000);
                System.out.println("添加2");
                String s = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("t2 结束future 任务！");
        }, "t2");


        t1.start();
        t2.start();
    }
}
