package ds.guang.majiang.server.network.buffer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guangyong.deng
 * @date 2022-01-17 17:00
 */
public class BufferTest {


    class Worker {

        private volatile int state;

        public WorkerServer server;

        public Worker(String name, WorkerServer server) {
            this.server = server;
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append("\"state\":").append(state)
                    .append('}');
            return sb.toString();
        }


        public void sendMessage(Integer message, String client) {
            System.out.println(Thread.currentThread().getName() + " 发送消息 " + message);
            server.getMessage(message, client);
        }


    }


    class WorkerServer  extends Thread {

        MessageBuffer<Integer> buffer1 ;

        Worker worker1;

        MessageBuffer<Integer> buffer2 ;

        Worker worker2;

        public WorkerServer() {
            this.buffer1 = new DsMessageBuffer<>();
            this.buffer2 = new DsMessageBuffer<>();
        }

        public void getMessage(Integer message, String client) {
            OverflowHandler overflowHandler = buffer -> System.out.println("加入消息失败！");
            if("t1".equals(client)) {
                buffer1.addMessage(message, overflowHandler);
                // 发送消息给 其他客户端
                sendMessage(message, "t2");
            }else {
                buffer2.addMessage(message, overflowHandler);
                sendMessage(message, "t1");
            }
        }


        public void sendMessage(Integer message, String client) {

            if("t1".equals(client)) {
                worker1.state = message;
            }else {
                worker2.state = message;
            }
        }

        public WorkerServer setWorker1(Worker worker1) {
            this.worker1 = worker1;
            return this;
        }

        public WorkerServer setWorker2(Worker worker2) {
            this.worker2 = worker2;
            return this;
        }
    }


    public void test() {

        WorkerServer server = new WorkerServer();
        Worker worker1 = new Worker("t1", server);

        Thread t1 = new Thread(() -> {

            worker1.sendMessage(1, "t1");
            while (true) {

                // 一直接受消息
                System.out.println("t1 当前状态： " + worker1.state);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "t1");

        Worker worker2 = new Worker("t2", server);
        Thread t2 = new Thread(() -> {

            // 模拟玩家 2
            // 一直发送消息
            // System.out.println("玩家2 发送消息！")
            for (int i = 2; i <= 5; i++) {
                worker2.sendMessage(i, "t2");
                System.out.println("t2 当前状态： " + worker2.state);
            }

        }, "t2");

        server.setWorker1(worker1);
        server.setWorker2(worker2);

        t1.start();
        t2.start();

    }

    public static void main(String[] args) {

       new BufferTest().test();
    }
}
