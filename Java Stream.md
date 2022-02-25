**Java Stream**

- JDK 9 
- RxJava

------

**JDK 9**

- 三个角色

  - Publisher：通过此接口来发布一个元素给有需求的消费者
  - Subscriber：每一个订阅者从 Publisher 那里获取所需的元素来进行消费
  - Subscription：Subscription 属于连接 Subscriber 和 Publisher 进行交流的中间人。在订阅者请求的时候需要用到它（请求元素和不再需要元素）

- 详细流程

  ![](C:\Users\guangyong.deng\Desktop\博客\Java-Stream\Stream-API.png)

  [https://github.com/guangmingdexin/stream-api.git]: 

- 函数式接口

  | 接口                  | 描述                                                 |
  | --------------------- | ---------------------------------------------------- |
  | **BiConsumer<T,U>**   | 代表了一个接受两个输入参数的操作，并且不返回任何结果 |
  | **BiFunction<T,U,R>** | 代表了一个接受两个输入参数的方法，并且返回一个结果   |
  | **Consumer<T>**       | 代表了接受一个输入参数并且无返回的操作               |
  | **Function<T,R>**     | 接受一个输入参数，返回一个结果                       |
  | **Supplier<T>**       | 无参数，返回一个结果                                 |
  |                       |                                                      |
  |                       |                                                      |

- 待解决问题

  - 关于 Subscriber 与 Subscription 的关系（一对一，还是多对一）
  - 为什么需要 Subscription 来作为中间操作
  

------

RxJava

- create
- subscribe
- cache
  - 每次在我们调用subscribe方法的时候，都会调用create方法，如何可以缓存起来使用
  - 依然需要产生订阅关系后才会执行元素的下发操作
  - 所以，使用cache不当就可能带来Bug，比如，在Observable发布一个无限流的时候，很可能会产生OutOfMemoryError
  - 数据的存储和订阅者的存储，以及将两者联系起来
- 无限流
- map
- flatMap

------

Reactor

- create

- subscribe

- generate

  - 不断产生数据源，直到 complete
  - 元素序列的产生可能是有状态的，需要用到某些状态对象此时可以使用generate方法的另一种形式：generate（Callable<S> stateSupplier，BiFunction<S，SynchronousSink<T>，S>generator），其中的stateSupplier用于提供初始的状态对象。在产生元素序列时，状态对象会作为generator的第一个参数传入，可以在对应的逻辑中对该状态对象进行修改，以供下一次产生时使用

- 所以我们按需加载，只有在产生订阅的时候，才真的进行内存分配，提高程序性能，可以使用 Supplier 方法

- publishOn

  - 从上游源获取元素，然后从所关联的Scheduler中获取一个worker，调用worker.schedule并向下游下发元素（是否说明生产源是异步的？）会将下发元素的消费逻辑交给worker.schedule执行，即元素的消费逻辑都会在这个worker所携带的线程中执行（应该是消费动作可以是异步的）

    ```java
     Flux.create(sink -> {
                // 初步排除问题是 数据下发动作被封装为了一个 worker
                sink.next("处理的数字： " + Math.random() * 100);
                // sink.complete();
            }).publishOn(Schedulers.elastic())
                    .subscribe(
                            consumer -> System.out.println(Thread.currentThread().getName() + " 创建的数字 " + consumer),
                            error -> System.out.println("抛出异常！" + error),
                            () -> {
                                System.out.println("任务完成！");
                            });
    ```

    ```bash
    elastic-2 创建的数字 处理的数字： 3.5106479667738055
    ```

    