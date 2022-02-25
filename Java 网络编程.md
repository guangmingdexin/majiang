Java 网络编程

**Java Socket**

- ServerSocket 解读

  - bind

    ```java
     // 绑定端口
     serverSocket = new ServerSocket(port);
    ```

    ```
    // java.net.ServerSocket#ServerSocket(int)
    // java.net.ServerSocket#bind
    ...
    ```

    ```
    // 最终执行位置
    // java.net.DualStackPlainSocketImpl#bind0
    ```

  - accept（默认为程序级别的阻塞，操作系统级别的同步IO）

    ```java
    // 阻塞等待直到有连接进来
    socket = serverSocket.accept();
    ```

    ```
    //java.net.ServerSocket#accept
    //java.net.ServerSocket#implAccept
    //java.net.AbstractPlainSocketImpl#accept
    //java.net.AbstractPlainSocketImpl#socketAccept
    //java.net.DualStackPlainSocketImpl#socketAccept
    //java.net.DualStackPlainSocketImpl#accept0 
    ```

    ```java
    // java.net.DualStackPlainSocketImpl#socketAccept
    // 返回 -1 表示这次accept没有发现有数据从底层返回
    int newfd = -1;
    InetSocketAddress[] isaa = new InetSocketAddress[1];
    // 如果设置的 timeout 值小于等于 0 直接进入阻塞
    if (timeout <= 0) {
        // 与操作系统交互来实现监听指定端口上是否有客户端接入
        newfd = accept0(nativefd, isaa);
    } else {
        // 否则可以通过设置指定 timeout 时间实现非阻塞
        configureBlocking(nativefd, false);
        try {
            waitForNewConnection(nativefd, timeout);
            newfd = accept0(nativefd, isaa);
            if (newfd != -1) {
                configureBlocking(newfd, true);
            }
        } finally {
            configureBlocking(nativefd, true);
        }
    }
    ```

    ```java
    // 设置程序阻塞时间，在指定等待时间后抛出异常，防止一直阻塞
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        getImpl().setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }
    ```

    - accept0 是一个本地方法，具体来说就是与操作系统交互来实现监听指定端口上是否有客户端接入，正是因为accept0在没有客户端接入的时候会一直处于阻塞状态，所以造成了我们程序级别的accept方法阻塞

    - 如何调整代码实现程序级别的非阻塞

      - 通过直接设置连接等待时间 timeout，如果超时则返回抛出异常
      - 多线程，线程池的方案

    - 程序级阻塞

      可以通过修改代码来实现非阻塞，这就是常用的阻塞/非阻塞

    - 操作系统级别阻塞

      无法通过代码来实现非阻塞，这就是同步/非同步

  - read（默认为程序级别的阻塞，操作系统级别的同步IO）

    ```java
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    ```

    ```
    //java.net.AbstractPlainSocketImpl#getInputStream
    ```

    ```java
    //java.net.AbstractPlainSocketImpl
    // 设置非阻塞同样设置 timeout
    public int read(byte b[], int off, int length) throws IOException {
        return read(b, off, length, impl.getTimeout());
    }
    int read(byte b[], int off, int length, int timeout) throws IOException {
        n = socketRead(fd, b, off, length, timeout);
        if (n > 0) {
            return n;
        }
    }
    ```

    - 第一次是设定的是ServerSocket级别的，第二次设定的客户端连接返回的那个Socket，两者不一样

  - 总结

    - 同步/异步是属于操作系统级别的，指的是操作系统在收到程序请求的IO之后，如果IO资源没有准备好的话，该如何响应程序的问题，同步的话就是不响应，直到IO资源准备好；而异步的话则会返回给程序一个标志，这个标志用于当IO资源准备好后通过事件机制发送的内容应该发到什么地方。

    - 阻塞/非阻塞是属于程序级别的，指的是程序在请求操作系统进行IO操作时，如果IO资源没有准备好的话，程序该怎么处理的问题，阻塞的话就是程序什么都不做，一直等到IO资源准备好，非阻塞的话程序则继续运行，但是会时不时的去查看下IO到底准备好没有呢

**Java Nio**

- Channel

  - Channel 是对 Socket 的增强包装

    ```java
    public interface Channel extends Closeable {
    
        /**
         * Tells whether or not this channel is open.
         */
        public boolean isOpen();
    
        /**
         * Closes this channel.
         */
        public void close() throws IOException;
    
    }
    ```

  - 关于 Channel 关闭

    - 一个Channel可能会被异步关闭和中断

    达到的具体的效果应该是如果线程在实现这个接口的的Channel中进行IO操作的时候，另一个线程可以调用该Channel的close方法。导致的结果就是，进行IO操作的那个阻塞线程会收到一个`AsynchronousCloseException`异常

    - 线程中断情况

    如果线程在实现这个接口的的Channel中进行IO操作的时候，另一个线程可能会调用被阻塞线程的`interrupt`方法(`Thread#interrupt()`)，从而导致Channel关闭，那么这个阻塞的线程应该要收到`ClosedByInterruptException`异常，同时将中断状态设定到该阻塞线程之上

    ```java
    public interface InterruptibleChannel extends Channel 
    {
    
        /**
         * Closes this channel.
         *
         */
        public void close() throws IOException;
    
    }
    ```

  - Channel 管理

    - 注册到 Selector 上`Channel`可以被`Selector`进行使用，而`Selector`是根据`Channel`的状态来分配任务的，那么`Channel`应该提供一个注册到`Selector`上的方法，来和`Selector`进行绑定，因为`Selector`是要根据状态值进行管理的，所以此方法会返回一个`SelectionKey`对象来表示这个`channel`在`selector`上的状态

      ```java
      //java.nio.channels.spi.AbstractSelectableChannel#register
      public final SelectionKey register(Selector sel, int ops, Object att) throws ClosedChannelException 
      {
      	synchronized (regLock) {
                  // 这里会判断 Channel 是否阻塞
                  // 如果为阻塞模式抛出异常，因为 Selector 下 Channel 必须为非阻塞
                  // 默认为 true
                  if (blocking)
                      throw new IllegalBlockingModeException();
                  SelectionKey k = findKey(sel);
                  if (k != null) {
                      k.interestOps(ops);
                      k.attach(att);
                  }
                  if (k == null) {
                      // New registration
                      synchronized (keyLock) {
                          k = ((AbstractSelector)sel).register(this, ops, att);
                          addKey(k);
                      }
                  }
                  return k;
              }
      }
      ```
  
      ```java
      // sun.nio.ch.SelectorImpl#register
      protected final SelectionKey register(AbstractSelectableChannel channel, int ops, Object att) {
              if (!(channel instanceof SelChImpl)) {
                  throw new IllegalSelectorException();
              } else {
                  SelectionKeyImpl key = new SelectionKeyImpl((SelChImpl)channel, this);
                  Set keys = this.publicKeys;
                  synchronized(this.publicKeys) {
                      this.implRegister(key);
                  }
      
                  key.interestOps(ops);
                  return key;
              }
          }
      ```
  
      ```java
      // sun.nio.ch.WindowsSelectorImpl#implRegister
      // select 
      ```
  
    - 注销 Channel ，Channel并没有直接提供解除注册的方法，那我们换一个思路，我们将Selector上代表其注册的Key取消不就可以了。这里可以通过调用`SelectionKey#cancel()`方法来显式的取消key。然后在`Selector`下一次选择操作期间进行对Channel的取消注册
  
      ```java
    //sun.nio.ch.AbstractSelectionKey#cancel
      valid = false;
      //还是调用Selector的cancel方法
      // 实际类型 SelectorImpl
      ((AbstractSelector)selector()).cancel(this);
      ```
    
      ```java
    //java.nio.channels.spi.AbstractSelector#cancel
      void cancel(SelectionKey k) {
          synchronized (cancelledKeys) {
          	cancelledKeys.add(k);
          }
      }
      ```
    
      ```java
    //在下一次select操作的时候来解除那些要求cancel的key，即解除Channel注册
      //sun.nio.ch.SelectorImpl#select(long)
      //sun.nio.ch.SelectorImpl#lockAndDoSelect
      inSelect = true;
      try {
      		synchronized (publicSelectedKeys) {
      			//重点关注此方法
      			return doSelect(action, timeout);
      		}
      } finally {
      	inSelect = false;
      }
      //sun.nio.ch.WindowsSelectorImpl#doSelect
      protected int doSelect(long timeout) throws IOException {
           //重点关注此方法
      /**Invoked by selection operations to process the cancelled-key set*/
          this.processDeregisterQueue();
          // 中断唤醒 select 方法 后面再看
          if (this.interruptTriggered) {
              this.resetWakeupSocket();
              return 0;
          } 
      }
      //sun.nio.ch.SelectorImpl#processDeregisterQueue
      void processDeregisterQueue() throws IOException {
              Set cancellKeys = this.cancelledKeys();
              synchronized(cancellKeys) {
                  // ... 省略部分就是通过迭代器删除集合中的 key
                  this.implDereg(key);     
              }
       }
      //sun.nio.ch.WindowsSelectorImpl#implDereg
      protected void implDereg(SelectionKeyImpl var1) throws IOException {
          // ...省略部分
          this.keys.remove(var1);
          this.selectedKeys.remove(var1);
          // 清理掉关联的Channel 中的 SelectionKey
          // 主要是移除数组中的 key，并将 valid 设置为 false
          this.deregister(var1);
          SelectableChannel var7 = var1.channel();
          if (!var7.isOpen() && !var7.isRegistered()) {
              // 如何关闭通道 Channel 的还不懂！
              ((SelChImpl)var7).kill();
          }
      }
      ```
    
    - 关闭 Channel
  
      论是通过调用`Channel#close`还是通过打断线程的方式来对Channel进行关闭，其都会隐式的取消关于这个Channel的所有的keys，其内部也是调用了`k.cancel()`
  
      ```java
    // java.nio.channels#close
      public void close() throws IOException;
      // java.nio.channels#close
      public void close() throws IOException;
      // java.nio.channels.spi.AbstractInterruptibleChannel#close
      public final void close() throws IOException {
          synchronized (closeLock) {
                  if (!open)
                      return;
                  open = false;
                  implCloseChannel();
              }
      }
      // java.nio.channels.spi.AbstractInterruptibleChannel#implCloseChannel
      protected abstract void implCloseChannel() throws IOException;
      // java.nio.channels.spi.AbstractSelectableChannel#implCloseChannel
       protected final void implCloseChannel() throws IOException {
           	// 应该是关闭 Channel 具体如何实现还不懂
              implCloseSelectableChannel();
              synchronized (keyLock) {
                 		// ... 省略
                  	// 将 channel 的所有 key 添加到 canceledKey 集合，在下一次 select 中移除
                      k.cancel();
                  }
              
          }
      ```
    
    - 关闭 Selector 差不多流程
  
    - Channel的阻塞模式与非阻塞模式
  
      - `Selector`的多路复用有关的操作必须基于非阻塞模式，所以在注册到`Selector`之前，必须将`channel`置于非阻塞模式，并且在取消注册之前，`channel`可能不会返回到阻塞模式（疑问为什么 Selector 的多路复用必须基于非阻塞模式？）
  
        selector 多路复用知识：
  
    - Channel支持网络socket的能力
  
      ```java
      // java.nio.channels.NetworkChannel
      public interface NetworkChannel extends Channel {
          // 将socket绑定到本地 SocketAddress上
          NetworkChannel bind(SocketAddress local) throws IOException;
      }
      //sun.nio.ch.ServerSocketChannelImpl#bind
      public ServerSocketChannel bind(SocketAddress var1, int var2) throws IOException {
              /**
              bind首先检查ServerSocket是否关闭，是否绑定地址， 如果既没有绑定也没关闭，则检查绑定的socketaddress是否正确或合法； 然后通过Net工具类的bind和listen，完成实际的ServerSocket地址绑定和开启监听，如果绑定是开启的参数小于1，则默认接受50个连接
              */
              synchronized(this.lock) {
                  	// ...
                  	//InetSocketAddress(0)表示绑定到本机的所有地址，由操作系统选择合适的端口
                      InetSocketAddress var4 = var1 == null ? new InetSocketAddress(0) : Net.checkAddress(var1);
                     //开启监听，如果参数backlog小于1，默认接受50个连接
                      Net.listen(this.fd, var2 < 1 ? 50 : var2);
                      Net.bind(this.fd, var4.getAddress(), var4.getPort());
                      return this;
                  }
              }
          }
      //sun.nio.ch.Net#bind(java.io.FileDescriptor, java.net.InetAddress, int)
      public static void bind(FileDescriptor fd, InetAddress addr, int port)
              throws IOException
          {
              bind(UNSPEC, fd, addr, port);
          }
      static void bind(ProtocolFamily family, FileDescriptor fd,
                          InetAddress addr, int port) throws IOException
      {
          //如果传入的协议域不是IPV4而且支持IPV6,则使用ipv6
          boolean preferIPv6 = isIPv6Available() &&
              (family != StandardProtocolFamily.INET);
          bind0(fd, preferIPv6, exclusiveBind, addr, port);
      }
      
      private static native void bind0(FileDescriptor fd, boolean preferIPv6,
                                          boolean useExclBind, InetAddress addr,
                                          int port)
          throws IOException;
      
      
      ```
    
      ```c
      // bind0 代码实现
      JNIEXPORT void JNICALL
      Java_sun_nio_ch_Net_bind0(JNIEnv *env, jclass clazz, jobject fdo, jboolean preferIPv6,
                                jboolean useExclBind, jobject iao, int port)
      {
          SOCKETADDRESS sa;
          int sa_len = 0;
          int rv = 0;
          //将java的InetAddress转换为c的struct sockaddr
          if (NET_InetAddressToSockaddr(env, iao, port, &sa, &sa_len,
                                        preferIPv6) != 0) {
              return;//转换失败，方法返回
          }
          //调用bind方法:int bind(int sockfd, struct sockaddr* addr, socklen_t addrlen)
          rv = NET_Bind(fdval(env, fdo), &sa, sa_len);
          if (rv != 0) {
              handleSocketError(env, errno);
          }
      }
      ```
    
      ```java
      //sun.nio.ch.ServerSocketChannelImpl#accept()
      public SocketChannel accept() throws IOException {
              //构建SocketChannelImpl，这个具体在SocketChannelImpl再说
          	// 这里也可以出来，每次 accept 都会创建一个新的 SocketChannel 进行操作
              SocketChannel sc = new SocketChannelImpl(provider(), newfd, isa);
          
          	//返回socketchannelimpl
              return sc;
          }
      ```
    
      ```c
      #include <sys/types.h>
      #include <sys/socket.h>
      
      int accept(int sockfd,struct sockaddr *addr,socklen_t *addrlen);
      ```
    
      - accept()系统调用主要用在基于连接的套接字类型，它提取出所监听套接字的等待连接队列中第一个连接请求，**创建一个新的套接字**，并返回指向该套接字的文件描述符。新建立的套接字不在监听状态，原来所监听的套接字也不受该系统调用的影响
  
        **备注：新建立的套接字准备发送send()和接收数据recv()**
  
      - 如果队列中没有等待的连接，套接字也没有被标记为Non-blocking，accept()会阻塞调用函数直到连接出现；如果套接字被标记为Non-blocking，队列中也没有等待的连接，accept()返回错误EAGAIN或EWOULDBLOCK
  
        **备注：一般来说，实现时accept()为阻塞函数，当监听socket调用accept()时，它先到自己的receive_buf中查看是否有连接数据包；若有，把数据拷贝出来，删掉接收到的数据包，创建新的socket与客户发来的地址建立连接；若没有，就阻塞等待**
  
        为了在套接字中有到来的连接时得到通知，可以使用**select()**或**poll()**。当尝试建立新连接时，系统发送一个可读事件，然后调用accept()为该连接获取套接字。另一种方法是，当套接字中有连接到来时设定套接字发送SIGIO信号
  
  - Selector
  
    - 管道 Pipe 的作用
  
      - 创建管道
  
        ```java
        Pipe.open()
        ```
  
        ```
        //java.nio.channels.Pipe#open
        //sun.nio.ch.PipeImpl#PipeImpl
        //sun.nio.ch.PipeImpl.Initializer.LoopbackConnector
        ```
  
        ```java
        @Override
        public void run() {
            ServerSocketChannel ssc = null;
            SocketChannel sc1 = null;
            SocketChannel sc2 = null;
        }
        ```
  
        这里即为创建`pipe`的过程，`windows`下的实现是创建两个本地的`socketChannel`，然后连接（连接的过程通过写一个随机数据做两个socket的连接校验），两个`socketChannel`分别实现了管道`pipe`的`source`与`sink`端
  
      - 管道的作用
  
        一个阻塞在`select`上的线程有以下三种方式可以被唤醒：
  
        - 有数据可读/写，或出现异常
        - 阻塞时间到，即`time out`
        - 收到一个`non-block`的信号。可由`kill`或`pthread_kill`发出
  
        所以，`Selector.wakeup()`要唤醒阻塞的`select`，那么也只能通过这三种方法，其中：
  
        - 第二种方法可以排除，因为`select`一旦阻塞，无法修改其`time out`时间。
        - 而第三种看来只能在`Linux`上实现，`Windows`上没有这种信号通知的机制
  
        假如我们多次调用`Selector.open()`，那么在`Windows`上会每调用一次，就会建立一对自己和自己的`loopback`的`TCP`连接；
  
        那就是如果想要唤醒`select`，只需要朝着自己的这个`loopback`连接发点数据过去，于是，就可以唤醒阻塞在`select`上的线程了
  
    - Selector的select方法的解读
  
      - PollArrayWrapper
      
      -  FileDescriptor
      
        **说明**：通过 FileDescriptor 这个类的实例来充当底层机器特定结构的不透明处理，表示打开文件，打开socket或其他字节源或接收器。
        文件描述符的主要用途是创建一个 FileInputStream或 FileOutputStream来包含它。
        注意: 应用程序不应创建自己的文件描述符
      
      - `windows`上`select`系统调用有最大文件描述符限制，一次只能轮询`1024`个文件描述符，如果多于1024个，需要多线程进行轮询，所以当多于 1024 个文件描述符时，会创建多个线程
      
        ```java
        //sun.nio.ch.WindowsSelectorImpl.SubSelector
        private final class SubSelector {
                private final int pollArrayIndex; // starting index in pollArray to poll
                // These arrays will hold result of native select().
                // The first element of each array is the number of selected sockets.
                // Other elements are file descriptors of selected sockets.
                // 保存发生read的FD
                private final int[] readFds = new int [MAX_SELECTABLE_FDS + 1];
                // 保存发生write的FD
                private final int[] writeFds = new int [MAX_SELECTABLE_FDS + 1];
                //保存发生except的FD
                private final int[] exceptFds = new int [MAX_SELECTABLE_FDS + 1];
        
                private SubSelector() {
                    this.pollArrayIndex = 0; // main thread
                }
        		// 调用本地方法，数组用来保存底层select的结果
                private native int poll0(long pollAddress, int numfds,
                     int[] readFds, int[] writeFds, int[] exceptFds, long timeout);
                     ...
        }
        ```
      
    
  - ByteBuffer
  
    - ByteBuffer 的创建
  
      ```java
      // 堆内存
      ByteBuffer buf = ByteBuffer.allocate(48);
      // 直接内存
      ByteBuffer buf = ByteBuffer.allocateDirect(48)
      ```
  
    - HeapBuffer 的读写
  
      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\nio-ByteBuffer.png)
  
      ```java
      // java.nio.HeapByteBuffer#get()
      public byte get() {
          return hb[ix(nextGetIndex())];
      }
      
      protected int ix(int i) {
          return i + offset;
      }
      
      // java.nio.Buffer#nextGetIndex()
      final int nextGetIndex() {                          
          if (position >= limit)
              throw new BufferUnderflowException();
          // 从这里也可以看出不是Buffer 不是线程安全的
          return position++;
      }
      ```
  
      相对于字节数组，是不用做各种参数校验，也不需要另外维护数组当前读写位置的变量了，麻烦的地方就是读写之间需要重置 position 的位置
  
    - 前置知识（了解堆外内存）
  
      - 用户态与内核态
  
        ![](C:\Users\guangyong.deng\Desktop\博客\Netty\nio-system-state.png)
  
        操作系统本质上是运行在硬件资源上的软件，所以有些指令是非常危险的，如果用错，将导致整个系统崩溃，如：清理内存、设置时钟等，所以，`CPU`将指令分为**特权指令**和**非特权指令**，对于那些危险的指令，只允许操作系统及其相关模块使用，普通的应用程序只能使用那些不会造成灾难的指令
  
      - 虚拟地址内存空间
  
        在`32`位机器上，操作系统中的进程的地址空间大小是`4G`（0x0000 0000 ~ 0xffff ffff）【按字节寻址，即最小单位为字节】，其中`0-3G`对应**用户空间**，`3G-4G`对应**内核空间**。假如我们物理机的内存只有2G大小呢？所以，这个**4G**的**地址空间**其实就是我们所说的**虚拟地址内存空间**
  
        进程使用虚拟地址内存中的地址，由操作系统协助相关硬件，把它“转换”成真正的物理地址。虚拟地址通过页表(`Page Table`)映射到物理内存，页表由操作系统维护并被处理器引用
  
        **如何进行虚拟地址映射到实际物理内存地址？**
  
        - 地址**一对一**的映射，如逻辑地址`0xc0000003`对应的物理地址为`0×3`，当出现物理空间大于虚拟空间时，无法充分使用物理空间
        - 借助高位内存地址空间做一个映射，当内核访问物理地址空间时，从高位内存地址中找一段大小相同的空闲逻辑地址，建立映射到想访问的那段物理内存，并使用完之后返回这段地址
  
        **补充知识！**
  
        位（bit）: 每一位的状态只有两种：0或1，在硬件上利用高电压和低电压两种信号实现
  
        字节（byte）: 字节由8个位组成，它是存储空间的基本计量单位
  
        地址线：用于传输地址信息的数据线，一根地址线可以通过高电平或低电平来区分1或0，因此一根地址线有两个状态：1或0，那么N根地址线可以表示2^n个不同的状态
  
        寻址：寻址指当CPU请求数据时获得该数据在内存上的位置的过程内存每一个存储位置的最小单元都可以储存0或1，即一个位的内容，而内存将8个位设定为一个存储空间的基本单位。而在地址线上每一个地址的编号便对应的是一个存储位置的最小的基本单位
  
        按字节寻址：**按字节寻址指一个地址线表示的数（即状态）与一个字节地址相对应**
  
      - 进程的虚拟空间
  
        进程在使用内存的时候，都不是直接访问内存物理地址的，进程访问的都是虚拟内存地址，然后虚拟内存地址再转化为内存物理地址
  
      - 系统IO调用
  
        在传统的文件IO操作中，都是调用操作系统提供的底层标准IO系统调用函数 read()、write() ，此时调用此函数的进程（在JAVA中即java进程）由当前的用户态切换到内核态，然后OS的内核代码负责将相应的文件数据读取到内核的IO缓冲区，然后再把数据从内核IO缓冲区拷贝到进程的私有地址空间中去，这样便完成了一次IO操作
  
      - 内存映射 
  
        堆外内存一般使用 DMA 传输数据（DMA 可以用来在设备内存与主存RAM之间直接进行数据交换，这个过程无需CPU干预）
  
        DMA读取数据这种操作涉及到底层的硬件，硬件一般是不能直接访问用户态空间的,也就是DMA不能直接访问用户缓冲区，普通IO操作需要将数据来回地在 用户缓冲区 和 内核缓冲区移动，这在一定程序上影响了IO的速度
  
        在用户进程虚拟内存地址和Kernel内存间直接建立映射关系。通过用户缓存和Kernel缓存的共享，用户程序的操作直接作用到Kernel内存，无需进行内存拷贝，省去了用户态到内核态的拷贝开销，直接在内存中操作文件对象
  
    - DirectByteBuffer
  
      - 内存分配
  
        ```java
        //java.nio.ByteBuffer#allocateDirect
        public static ByteBuffer allocateDirect(int capacity) {
            return new DirectByteBuffer(capacity);
        }
        
        DirectByteBuffer(int cap) {                   // package-private
        
            super(-1, 0, cap, cap);
            // 判断是否需要页面对齐，通过参数-XX:+PageAlignDirectMemory控制，默认为false
            // 关于页面知识：后面慢慢了解
            boolean pa = VM.isDirectMemoryPageAligned();
             // 获取每页内存大小
            int ps = Bits.pageSize();
             // 分配内存的大小，如果是按页对齐方式，需要再加一页内存的容量
            long size = Math.max(1L, (long)cap + (pa ? ps : 0));
            // 用Bits类保存总分配内存(按页分配)的大小和实际内存的大小
            // 可以通过 "-XX:MaxDirectMemorySize=<size>" 设置堆外内存
            Bits.reserveMemory(size, cap);
        
            long base = 0;
            try {
                // 实际分配堆外内存
                base = unsafe.allocateMemory(size);
            } catch (OutOfMemoryError x) {
                // 分配失败，调整记录的内存数量
                Bits.unreserveMemory(size, cap);
                throw x;
            }
            // 初始化分配内存空间，指定内存大小，该空间中每个位置值为0
            unsafe.setMemory(base, size, (byte) 0);
            // 设置内存起始地址，如果需要页面对齐，
             // 则判断base是否有对齐，有且不是一个页的起始位置则通过计算进行地址对齐操作
            if (pa && (base % ps != 0)) {
                // Round up to page boundary
                address = base + ps - (base & (ps - 1));
            } else {
                address = base;
            }
            // 创建一个cleaner ，最终通过 Cleaner 回收对象引用
            cleaner = Cleaner.create(this, new Deallocator(base, size, cap));
            att = null;
        }
        /****************java.nio.Bits***************/
        /**
        * 当出现堆外内存不足时，会重新尝试的次数
        * 每次尝试过后等会等待一定的时间，分别是：1, 2, 4, 8, 16, 32, 64, 128, 256 (total 511 ms ~ 0.5 s)，主要是等待 JVM 做 GC，看能不能腾出空间
        * 所以 OOM 异常一般会延迟 0.5 s 才会抛出，原因就是会不断重试九次
        */
        private static final int MAX_SLEEPS = 9;
        // size：根据是否按页对齐，得到的实际页数，可以通过这个页数，计算实际内存大小
        // cap：用户指定需要的内存大小(<=size)
        static void reserveMemory(long size, int cap) {
            // 获取最大可以申请的对外内存大小
            // 可通过参数-XX:MaxDirectMemorySize=<size>设置这个大小
            if (!MEMORY_LIMIT_SET && VM.initLevel() >= 1) {
                MAX_MEMORY = VM.maxDirectMemory();
                MEMORY_LIMIT_SET = true;
            }
        	
            // 有足够空间可供分配，则直接return，否则，继续执行下面逻辑，尝试重新分配
            if (tryReserveMemory(size, cap)) {
                return;
            }
            // 一个预留的特权类，可以跨越 Java 的各种访问权限，直接访问到方法
            final JavaLangRefAccess jlra = SharedSecrets.getJavaLangRefAccess();
            
            // 实际调用的就是 Referece#tryHandlePendingReference，只是为什么没有参数也可以匹配到，奇怪？
            // 清理挂起的对象引用
            while (jlra.tryHandlePendingReference()) {
                if (tryReserveMemory(size, cap)) {
                    return;
                }
            }
            
            // 如果已经没有足够空间，则尝试GC
            // TODO:按理来说，gc 应该无法直接管理堆外内存，所以这里 gc 的目的何在？
            // DirectBuffer 的内存是分为两部分，一是 Java 堆内存的对象引用，二是 申请的堆外内存空间，GC 是无法直接回收堆外内存的，所以这里的目的应该是回收对象引用，通过回收DirectBuffer对象引用之后会触发一个钩子（回调）函数，进行堆外内存的回收
            // 其最终回收方法仍然是 UNSAFE.freeMemory
            System.gc();
            
            // 下面的就是尝试申请分配内存失败，不断重试，直到超过 MAX_SLEEPS
            
        }
        // 非常简单的方法，就是查看分配的直接内存容量是否超过设置的最大限制
        // 注意：这里并没有真正分配内存，只是设置了一个数字而已
        private static boolean tryReserveMemory(long size, int cap) {}
        ```
      
        - 前置知识：Reference 类学习
      
      
        ```java
        /**************java.lang.ref#Reference**************/
        // 主要是负责管理对象在JVM内存中的状态
        public abstract class Reference<T> {
            
            /**
        	 * Reference 会有四种状态
        	 * Active:新创建的对象是这个状态（Newly-created instances are Active）
        	 * Pending:在 pending-Reference 队列中的对象，并将会被 Reference-handler 处理
        	 *			可以理解为将要被回收的对象
        	 * Enqueued:注册在 ReferenceQueue 中的对象
        	 * Inactive:最终的状态，不能再变为其它状态
        	 *
        	 * 还有一大段注释，大概就是通过 queue 和 next 的状态来确认对象内存的状态
        	 */
            
            // 表示其引用的对象
            private T referent;
            
            // 当对象即将被回收时，整个reference对象，而不仅仅是被回收的对象，会被放到queue 里面
            // 执行者为垃圾收集器
            // 可以在对象被回收时，有一些其他处理
            volatile ReferenceQueue<? super T> queue;
            
            /**
            * 即当前引用节点所存储的下一个即将被处理的节点
            * When active:   NULL
            *	   pending:  this	
            *	   Enqueued:   next reference in queue (or this if last)
            *	   Inactive:   this
            * 只有引用在 queue 中才有意义，否则不是为 null 就是为自身	
            * 为了描述相应的状态值，在放到队列当中后，其queue就不会再引用这个队列了。而是引用一个特殊的 ENQUEUED（内部定义的一个空队列）
            */
            volatile Reference next;
            
            /**
            * 表示要处理的 pending list 中的下一个元素（TODO：这个和 next 的区别在哪里？一个处理引用？一个处理对象？）
            * 并且是由 JVM 维护
            * When active: discovered reference list 的下一个元素 (or this if last)
            *	   pending: pending list 的下一个元素 (or null if last)
            *
            *
            * 整个流程大概如下：
            *	pending 与 discovered 组成一个单向链表，pending为链表的头节点 并且为全局唯一
            *	discovered为链表当前Reference节点指向下一个节点的引用，当对象不可达时（没有其他的强引用时），垃圾回收器会将这个 Reference 放入 pending 中，并由 由ReferenceHander线程来处理这个队列中的引用，如果自定义了 ReferenceQueue，则进入 ReferenceQueue，否则直接回收掉
            *		
            */
            transient private Reference<T> discovered;
            
            /**
            * pending指向的是pending list链表的head
            * 这个 pending list 是在哪里？
            * JVM 收集器会添加引用到这个列表，直到Reference-handler线程移除了它们。这个列表使用 discovered 字段来连接它下一个元素（即 pending 的下一个元素就是discovered对象。r = pending; pending = r.discovered）
            */
             private static Reference<Object> pending = null;
            
            /**
        	* 类启动时，会启动 ReferenceHandler 线程，一直执行 tryHandlePending(true)
        	*/
            
                static boolean tryHandlePending(boolean waitForNotify) {
                Reference<Object> r;
                Cleaner c;
                try {
                    // 此处需要加全局锁，因为除了reference 线程，gc 线程也会操作pending队列
                    synchronized (lock) {
                        // pending list不为null, 说明有需要处理的引用
                        if (pending != null) {
                            r = pending;
                            c = r instanceof Cleaner ? (Cleaner) r : null;
                            // 从pending list中删除 r
                            // TODO:为什么如此设计？
                            pending = r.discovered;
                            r.discovered = null;
                        } else {
        					// 没有需要处理的引用, 就wait
                            // TODO:谁来唤醒？
                            if (waitForNotify) {
                                lock.wait();
                            }
                            // retry if waited
                            return waitForNotify;
                        }
                    }
                } catch (OutOfMemoryError x) {
                    Thread.yield();
                    // retry
                    return true;
                } catch (InterruptedException x) {
                    // retry
                    return true;
                }
        
                // 如果是 Cleaners 对象，则直接清理，不加入队列，说明 Cleaners 是特殊的
                if (c != null) {
                    c.clean();
                    return true;
                }
        		// 如果定义了 ReferenceQueue，则加入队列中
                ReferenceQueue<? super Object> q = r.queue;
                if (q != ReferenceQueue.NULL) q.enqueue(r);
                return true;
            }
        }
        
        /*****************ReferenceQueue*************************/
        // 单向链表，只能操作队头，实际上是一个栈，先进后出
        public class ReferenceQueue<T> {
            // 类似于标志位的作用，用来判断 Reference 的状态
            static ReferenceQueue<Object> NULL = new Null<>();
            static ReferenceQueue<Object> ENQUEUED = new Null<>();
            
            // 队列头
        	private volatile Reference<? extends T> head = null;
            // 不能重复入队，直接设置成标志位
            r.queue = ENQUEUED;
            // 从队列头部入队
            r.next = (head == null) ? r : head;
            head = r;
        }
        ```
      
        注意：通过源码注释以及相关资料的查询，可以知道一个对象引用加入 Pending list 的条件有两条
      
        1. 在构造 Reference 时，定义了 ReferenceQueue
      
        2. 该对象可达性变成了不可达（或者说没有其他强引用了 TODO : 是否包含了 Reference ? 以及如何维护 pending list ? 还需要查看一下 JVM ，有空的再说）
      
        3. 状态转换，reference 状态只需要通过成员变量next和queue来判断
      
           ![](C:\Users\guangyong.deng\Desktop\博客\Netty\reference-state-change.png)
      
      - 内存释放
      
        ```java
        /*****************Cleaner*************/
        // 其实内存释放逻辑上面已经说的很清楚了 分为两部分进行 1.堆内对象引用  2.堆外内存回收
        
        ```
      
      - 线程安全
      
        基本没有线程安全，会涉及到的线程只有 Reference 线程，以及回收器线程
        
      - 其他几种 Reference 
      
        | 引用类型                   | 特点                                     |
        | -------------------------- | ---------------------------------------- |
        | Strong Reference（强引用） | 只要存在强引用，就不会 VM 垃圾回收       |
        | SoftReference（软引用）    | 只存在软引用，则内存空间不足时，会被回收 |
        | WeakReference（弱引用）    | 只存在弱引用，会被 VM 回收               |
        | PhantomReference（虚引用） |                                          |
  
  ------

**Netty**

![](C:\Users\guangyong.deng\Desktop\博客\Netty\netty-components.png)



- Event-Loop

  - 构建线程池，创建执行线程

    ```java
    // 创建 NioEventLoopGroup 
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // io.netty.channel.nio.NioEventLoopGroup#NioEventLoopGroup
    public NioEventLoopGroup(int nThreads, Executor executor) {
     	// 这里传入了 Nio 的 Selector
        this(nThreads, executor, SelectorProvider.provider());
    }
    // io.netty.channel.nio.NioEventLoopGroup#NioEventLoopGroup
     public NioEventLoopGroup(...) {
         // 这里使用了工厂模式，预留了一个工厂接口，可以每次创建不同的 SelectStrategy
         /**
         *SelectStrategy:提供一种能够控制多路选择的能力，比如当有事件到来的时候，可以延迟阻塞或者跳过
         *此次循环（这里翻译的不是很好，也不是完全理解）	
         */
            this(nThreads, threadFactory, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
        }
    
        public NioEventLoopGroup(...) {
            super(nThreads, threadFactory, selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject());
        }
    // io.netty.channel.MultithreadEventLoopGroup#MultithreadEventLoopGroup
        protected MultithreadEventLoopGroup(...) {
            // 如果传入的创建线程数为 0 则创建默认的线程数，默认为 cup 的 2倍
            super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
        }
    //io.netty.util.concurrent.MultithreadEventExecutorGroupr#MultithreadEventExecutorGroup
       protected MultithreadEventExecutorGroup(...) {
            // 创建一个用户级别的任务调度器,
            this(nThreads, threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory), args);
        }
    //io.netty.util.concurrent.MultithreadEventExecutorGroupr#MultithreadEventExecutorGroup
     protected MultithreadEventExecutorGroup(...) {
         // 创建一个线程池选择器工厂，可以创建不同的线程池选择器
         // 创建一个线程池选择器，需要传入一个 EventExecutor 数组
         //  EventExecutorChooser（线程选择器）：返回一个 EventExecutor（线程池）
         // 默认使用轮询的方式从 EventExecutor 数组中选择线程池
            this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
        }
    
    //io.netty.util.concurrent.MultithreadEventExecutorGroupr#MultithreadEventExecutorGroup
    protected MultithreadEventExecutorGroup(...) {
        	// 默认为 null
            // 创建一个任务调度器，并将 NioEventLoop 线程封装成  FastThreadLocalThread
        
        	if (executor == null) {
                executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
            }
            // 创建一个线程池数组
        	// 这里的 EventExecutor 的实际类型为 NioEventLoop
            children = new EventExecutor[nThreads];
    
            for (int i = 0; i < nThreads; i ++) {
                boolean success = false;
                try {
                    // 为每个线程池绑定一个多路复用选择器（args 中有一个 selector）
                    // 进入 NioEventLoopGroup 的 newChild
                    children[i] = newChild(executor, args);
                    success = true;
                } catch (Exception e) {
                    // TODO: Think about if this is a good exception type
                    throw new IllegalStateException("failed to create a child event loop", e);
                } finally {
                   //...
                }
            }
    		// 选择调用 EventLoop 的方式
            chooser = chooserFactory.newChooser(children);
    		// 为每一个 e 绑定监听器
    		// 将 children 加入 set
        }
    
    // io.netty.channel.nio.NioEventLoopGroup#NioEventLoopGroup
        protected EventLoop newChild(Executor executor, Object... args) throws Exception {
            // 默认为 null
            EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory) args[3] : null;
            // 创建一个 NioEventLoop
            return new NioEventLoop(this, executor, (SelectorProvider) args[0],
                ((SelectStrategyFactory) args[1]).newSelectStrategy(), (RejectedExecutionHandler) args[2], queueFactory);
        }
    ```
    
    ```java
    // 默认线程池选择器工厂实现
    public final class DefaultEventExecutorChooserFactory implements EventExecutorChooserFactory {
        
        @SuppressWarnings("unchecked")
        @Override
        public EventExecutorChooser newChooser(EventExecutor[] executors) {
            if (isPowerOfTwo(executors.length)) {
                // 如果长度是 2的次方，则通过 &操作确定轮询下标（具体原理可以查看 hashmap）
                return new PowerOfTwoEventExecutorChooser(executors);
            } else {
                // 如果长度不是 2的次方，则通过 % 操作确定轮询下标
                return new GenericEventExecutorChooser(executors);
            }
        }
    	// 这个方法可以快速判断 val 是否是 2的次方
        // 负数的二进制 = 正数的二进制补码 + 1
        // 8 = 0000 1000 -> (补码) 1111 0111
        private static boolean isPowerOfTwo(int val) {
            return (val & -val) == val;
        }
    
    }
    ```
    
  - NioEventLoop
  
    - 开启Selector并初始化
  
      ```java
      // io.netty.channel.nio.NioEventLoop#NioEventLoop
      NioEventLoop(...) {
          // 重点关注
      	 final SelectorTuple selectorTuple = openSelector();
      }
      // io.netty.channel.nio.NioEventLoop#openSelector
      private SelectorTuple openSelector() {
          	//...
            	// 获取 nio 原装的 selector
              unwrappedSelector = provider.openSelector();
            	/**
            	* Netty为Selector设置了优化开关，如果开启优化开关，则通过反射加载sun.nio.ch.SelectorImpl对象，并通过已经优化过的SelectedSelectionKeySet替换sun.nio.ch.SelectorImpl对象中的selectedKeys和publicSelectedKeys两个HashSet集合
      其中，selectedKeys为就绪Key的集合，拥有所有操作事件准备就绪的选择Key；publicSelectedKeys为外部访问就绪Key的集合代理，由selectedKeys集合包装成不可修改的集合
            	*/
          	// 默认不开启优化开关，直接返回
              if (DISABLE_KEY_SET_OPTIMIZATION) {
                  return new SelectorTuple(unwrappedSelector);
              }
      		// 通过反射加载 SelectorImpl
              Object maybeSelectorImplClass = Class.forName(
                                  "sun.nio.ch.SelectorImpl",
                                  false,
                                  PlatformDependent.getSystemClassLoader());
          	// ... 大量判断
      		// SelectedSelectionKeySet 与 selectedKeys的区别：用数组替代了HashSet，重写了add()和iterator()方法，使数组的遍历效率更高
          	// 思考？为什么会使用数组替代 HashSet 优点缺点在哪？
          			// 数组不用考虑出现hash 冲突，设计 hash 算法，高效简单，但无法保证集合元素不重复（需结合具体业务考虑：比如这里其实不需要利用到 HashSet 的大部分特点）
              final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
      		selectedKeys = selectedKeySet;
              return new SelectorTuple(unwrappedSelector,
             new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
          }
      ```
      
      - 这里 nio 对 selectedKeys 的增强运用是通过类似 Collections#unmodifiableSet 方法实现的，包含了原始 Set 但不提供更改方法，一旦更改发生报错（值得借鉴！） 
      
        ```java
        protected Set<SelectionKey> selectedKeys = new HashSet();
        private Set<SelectionKey> publicSelectedKeys;
        
        protected SelectorImpl(SelectorProvider var1) {
            if (Util.atBugLevel("1.4")) {
                ...
            }else {
                 this.publicSelectedKeys = Util.ungrowableSet(this.selectedKeys);
            }
        }
        ```
      
      - run 方法分为三个部分
        - select(boolean oldWakenUp)，用来轮询就绪的 Channel
        - process SelectedKeys，用来处理轮询到的 SelectionKey
        - runAllTasks 主要目的是执行taskQueue队列和定时任务队列中的任务，如心跳检测、异步写操作等
      
      ```java
      // io.netty.channel.nio.NioEventLoop#run
      protected void run() {
          for (;;) {
              /**
      		* 1.开始会根据任务队列中是否存在任务进行下一步
      		*	1.1 如果没有任务，返回 SelectStrategy.SELECT
      		*	1.2 如果有任务，则执行 selector.selectNow()[非阻塞的轮询，立即返回]
      		*			那这么说明 Nio 也可以同步异步？
      		*/
              switch (selectStrategy.calculateStrategy(selectNowSupplier, hasTasks())) {
                      case SelectStrategy.CONTINUE:
                          continue;
                      case SelectStrategy.BUSY_WAIT:
                          // fall-through to SELECT since the busy-wait is not supported with NIO     
                      case SelectStrategy.SELECT:select(wakenUp.getAndSet(false));
      /**
      *wakenUp唤醒动作可能在NioEventLoop线程运行的两个阶段被触发，第一阶段有可能在NioEventLoop线程运行于wakenUp.getAndSet(false)与selector.select(timeoutMillis)之间。此时selector.select能立刻返回，最新任务得到及时执行
      */
                          if (wakenUp.get()) {
                              selector.wakeup();
                          }
                          // fall through
                      default:
              	}
          	} catch (IOException e) {
                  // 抛出异常则重建 Selector
                  rebuildSelector0();
                  handleLoopException(e);
              	continue;
               }
              // 无效的 Key 个数
          	cancelledKeys = 0;
              needsToSelectAgain = false;
              // EventLoop 处理 I/O 和执行 任务的时间分配
              // ioRatio = 0.5 / 执行 I/O 和执行任务的时间各占一半
              final int ioRatio = this.ioRatio;
          
          	if (ioRatio == 100) {
                  try {
                      processSelectedKeys();
                  } finally {
                      // Ensure we always run tasks.
                      runAllTasks();
                  }
              } else {
                  final long ioStartTime = System.nanoTime();
                  try {
                      // 处理第一部分轮询到的就绪Key
                      // 从这里应该可以解释必须至少轮询一次的原因了：处理 Channel 上的 I/O 操作
                      processSelectedKeys();
                  } finally {
                      // Ensure we always run tasks.
                      final long ioTime = System.nanoTime() - ioStartTime;
                      runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                  }
              }
      	}     
      }
      // io.netty.channel.nio.NioEventLoop#processSelectedKeys
      private void processSelectedKeys() {
          // 判断优化后的 selectedKeys 是否为空
          if (selectedKeys != null) {
              // 优化处理
              processSelectedKeysOptimized();
          } else {
              // 原始处理
              processSelectedKeysPlain(selector.selectedKeys());
          }
      }
      // io.netty.channel.nio.NioEventLoop#processSelectedKeysOptimized
      private void processSelectedKeysOptimized() {
          for (int i = 0; i < selectedKeys.size; ++i) {
               final SelectionKey k = selectedKeys.keys[i];
               // null out entry in the array to allow to have it GC'ed once the Channel close
              // See https://github.com/netty/netty/issues/2363
              selectedKeys.keys[i] = null;
              // 根据 key 的就绪事件触发对应的事件方法
              processSelectedKey(k, (AbstractNioChannel) a);
          }
      }
      
      // io.netty.channel.nio.NioEventLoop#select(boolean oldWakenUp)[重点方法！]
      Selector selector = this.selector;
              try {
                  // 轮询次数
                  int selectCnt = 0;
                  // 获取当前系统时间（纳秒级）
                  long currentTimeNanos = System.nanoTime();
                  // 获取定时任务的触发时间
                  //#delayNanos 返回上一个定时任务还需要执行的时间
                  long selectDeadLineNanos = currentTimeNanos + delayNanos(currentTimeNanos);
      
                  for (;;) {
                      // 获取距离定时任务触发的时长（四舍五入）
                      long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
                      // 已触发或已超时
                      /**
                      * 1.当有定时/周期性任务即将到达执行时间
                      * 2.NioEventLoop的线程收到了新提交的任务上来等待着被处理
                      * NioEventLoop 则退出select方法转而去执行任务
                      */
                      if (timeoutMillis <= 0) {
                          // 若之前未执行过 select, 则调用非阻塞的 selectNow() 方法
                          if (selectCnt == 0) {
                              // 如果一次都没有轮询过
                              // 则轮询一次，并将 SelectionKeys 清空，为什么需要清空代理的 selectionKeys 集合（防止上一次轮询的结果影响，想一想 Nio 的 SelectionKeys 处理模式，每次都需要将已经处理的 SelectionKeys 移除 Set）
                              // （腾出手来去处理任务 ？ 如果这里不轮询会有什么情况？） - 
                              selector.selectNow();
                              selectCnt = 1;
                          }
                          // 跳出循环，处理 I/O 事件和任务
                          break;
                      }
      
                      // 当任务队列中有任务，且唤醒标志为 false时，需要调用 selectNow() 方法
                      // 否则任务得不到及时处理，可能需要阻塞等待超时
                      // 检测到有任务，并未设置预唤醒标识（Netty 4 之后）
                      /**
                      * 补充知识：关于调用 Selector#wakeup的条件
                      * 1.成员变量addTaskWakesUp为false
                      * 2.当提交上来的任务不是一个NonWakeupRunnable任务
                      * 3.执行提交任务的线程不是EventLoop所在线程
                      * 4.当wakenUp成员变量当前的值为false
                      */
                      if (hasTasks() && wakenUp.compareAndSet(false, true)) {
                          selector.selectNow();
                          selectCnt = 1;
                          break;
                      }
      				// 阻塞检测就绪 Channel
                      int selectedKeys = selector.select(timeoutMillis);
                      // 检测次数加一，检测是否为空轮询
                      selectCnt ++;
      				// 1.有就绪 Channel
                      // 2.
                      if (selectedKeys != 0 || oldWakenUp || wakenUp.get() || hasTasks() || hasScheduledTasks()) {
                          // - Selected something,
                          // - waken up by user, or
                          // - the task queue has a pending task.
                          // - a scheduled task is ready for processing
                          break;
                      }
                      if (Thread.interrupted()) {
                          selectCnt = 1;
                          break;
                      }
      
                      long time = System.nanoTime();
                      if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                          // timeoutMillis elapsed without anything selected.
                          selectCnt = 1;
                      } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 &&
                              selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                          // 空轮询次数超过多少，直接重建 selector
                          selector = selectRebuildSelector(selectCnt);
                          selectCnt = 1;
                          break;
                      }
                      currentTimeNanos = time;
                  }
              }
      }
      ```
      
      主要目的是轮询看看是否有准备就绪的Channel。在轮询过程中会调用NIO Selector的selectNow()和select(timeoutMillis)方法
      
      - 如下问题
        - 定时任务 - 一般会有哪些任务可以被添加到任务队列中，加入哪个队列？由哪个线程执行？
        - 定时任务 - 作用是什么？
        - 任务队列的作用是什么
        - 唤醒标志的作用是什么
        - 线程的作用
        - 如何处理线程中断
      - 查询资料可知
        - 线程会一部分时间执行IO任务，一部分时间执行队列中的任务
        - 队列中的任务，添加之后也不会是马上执行，等待任务时间到了才会去执行
        - 任务队列
          - taskQueue：非IO任务
          - tailTasks：预留扩展功能点
          - scheduledTaskQueue：延时任务
      
    - 把ServerSocketChannel注册到Selector上
  
- io.netty.channel

  - Channel

    - 类结构

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\Channel.png)
      
      

  - AbstractChannel

    - 作用：融合了Netty的线程模型、事件驱动模型，但由于网络I/O模型及协议种类比较多，除了TCP协议，Netty还支持很多其他连接协议，并且每种协议都有传统阻塞I/O和NIO（非阻塞I/O）版本的区别。不同协议、不同阻塞类型的连接有不同的Channel类型与之对应，因此AbstractChannel并没有与网络I/O直接相关的操作

    - 代码

      ```java
       private final Channel parent;
      // 实现具体的连接与读/写数据，如网络的读/写、链路关闭、发起连接等。命名为Unsafe表示不对外提供使用，并非不安全
       private final Unsafe unsafe;
       // 一个Handler的容器，也可以将其理解为一个Handler链。Handler主要处理数据的编/解码和业务逻辑
       private final DefaultChannelPipeline pipeline;
       // 每个Channel对应一条EventLoop线程
       private volatile EventLoop eventLoop;
      ```

      ```java
      // io.netty.channel.AbstractChannel#AbstractChannel
      protected AbstractChannel(Channel parent) {
          this.parent = parent;
          id = newId();
          // 创建一个 Unsafe 对象
          unsafe = newUnsafe();
          pipeline = newChannelPipeline();
      }
      // io.netty.channel.AbstractChannel#newUnsafe
      protected abstract AbstractUnsafe newUnsafe();
      
      // bind ... read... write 方法都一样
      public ChannelFuture bind(SocketAddress localAddress) {
          // I/O事件的处理，都委托给ChannelPipeline处理
          return pipeline.bind(localAddress);
      }
      ```

  - AbstractUnsafe

    - 类结构

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\AbstractUnsafe.png)

    - 作用：AbstractUnsafe，其具体的实现类在AbstractChannel的子类中，AbstractUnsafe的大部分方法都采用了模板设计模式，具体的实现细节由其子类完成，定义了 I/O 事件处理的基本框架 -- （与 pipeline 的功能差异在哪里？）

      unsafe 的作用是完成 I/O 的处理，包括 socket 的连接，从底层系统缓冲区读取数据，写入数据到系统缓冲区

      pipline 是处理用户的业务代码逻辑，比如有一个连接事件发送后，会经过什么样的其他处理，是缓存用户信息，还是写入日志，都可以由用户自定义的 handler 来决定

    - 代码

      ```java
      /**********************AbstractUnsafe*************/
      // 用来将 Channel 和 EventLoop 绑定
      public final void register(EventLoop eventLoop, final ChannelPromise promise) {
           AbstractChannel.this.eventLoop = eventLoop;
           // 如何当前线程是 eventLoop 直接执行
           if (eventLoop.inEventLoop()) {
                      register0(promise);
                  } else {
                      try {
                          // 否则，封装为一个任务提交给 eventLoop 执行
                          eventLoop.execute(new Runnable() {
                              @Override
                              public void run() {
                                  register0(promise);
                              }
                          });
                      }
           		}
      }
      
      private void register0(ChannelPromise promise) {
          
          // ...
          // 模板方法，由子类实现去完成，父类规定好步骤就行了
          doRegister();
          // ...
          // ServerSocketChannel 接受的 Channel 此时已被激活
          if (isActive()) {
              if (firstRegistration) {
                  // 首次注册且激活触发Channel激活事件
                  pipeline.fireChannelActive();
              } else if (config().isAutoRead()) {
                  // 在通道 active时，可以自动触发读事件，当有一个读事件在等待时，这个方法什么也不会做			
                  // nio channel 中 #isActive: ch.isOpen() && ch.isConnected()
                  beginRead();
              }
          }
      }
      
      public final void bind(...){
          // 子类去实现
          doBind();   
          // 触发 pipeline 的 active事件
      }
      
      // TODO: 为啥没有 connect 操作？
      // 不同协议、不同阻塞类型的连接都是非常不同的，应该由子类根据具体情况去实现！（猜测）
      public final void disconnect(final ChannelPromise promise) {
          // ...
          doDisconnect();
          // 触发 inactive 事件
      }
      
      public final void deregister(final ChannelPromise promise) {
      	doDeregister();
          // 触发 unregistered 事件
      }
      
      public final void write(Object msg, ChannelPromise promise) {
          // outboundBuffer为空表示Channel正在关闭，禁止写数据
          if (outboundBuffer == null) {
          }
          
          // 判断 msg 的类型
          msg = filterOutboundMessage(msg);
          // 判断size
          
          // 加入缓冲区
          outboundBuffer.addMessage(msg, size, promise);
      }
      ```

  - AbstractNioChannel

    - 类结构

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\AbstractNioChannel.png)

    - 作用：已经将Netty的Channel和JavaNIO的Channel关联起来了

    - 代码

      ```java
      // 真正用到的 NIO Channel
      private final SelectableChannel ch;
      // 监听感兴趣的事件
      protected final int readInterestOp;
      // 注册到 Selector 后获得的 key
      volatile SelectionKey selectionKey;
      ```

      ```java
      // io.netty.channel.nio.AbstractNioChannel#doRegister
      protected void doRegister() throws Exception {
          for (;;) {
              // #javaChannel() 返回 nio channel
              // 把 channel 注册到 eventLoop 上的 selector 上
              // 并设置关注的事件
               selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
          }
      }
      ```

      ```java
      // io.netty.channel.nio.AbstractNioChannel。AbstractNioUnsafe#connect
      public final void connect(..., final ChannelPromise promise) {
          if (connectPromise != null) {
              // 确定没有正在进行的连接
              throw new ConnectionPendingException();
          }
          /**
          * 在远程连接时，会出现 3 种情况
          * 1. 连接成功，返回 true
          * 2. 暂时没有连接上，服务端没有返回 Ack 应答，连接结果不确定，返回 false
          * 3. 连接失败，直接抛出 I/O 异常
          * 由于协议和 I/O 模型不同，连接的方式也不一样，因此具体实现由其子类完成
          */
          if (doConnect(remoteAddress, localAddress)) {
              // 连接成功后会触发 ChannelActive 事件
              // 最终会将 NioSocketChannel 中的 SelectionKey 设置为 SelectionKey.OP_READ
              // 用于监听网络读操作（具体位置没有找到！）
              fulfillConnectPromise(promise, wasActive);
          }
      }
      // io.netty.channel.nio.AbstractNioChannel。AbstractNioUnsafe#finishConnect
      public final void finishConnect() {
          /**
          * 判断连接结果，由 SocketChannel 实现
          */
          doFinishConnect();
          // 负责将 SocketChannel 修改为监听读操作
          // 用来监听网络的读写事件
          fulfillConnectPromise(connectPromise, wasActive);
      }
      ```
      
      ```java
      /*************** NioUnsafe ****************/
      // 对应NIO中的JDK实现的Channel
      SelectableChannel ch(); 
      // 完成连接（SelectableChannel设置为非阻塞模式时，connect()方法会立即返回，此时连接操作可能没有完成，如果没有完成，则需要调用JDK的finishConnect()方法完成连接操作）
      void finishConnect();   
      // 从JDK的Channel中读取数据
      void read();    
      // 强制刷新数据
      void forceFlush(); 
      
      /*************  AbstractNioUnsafe **********/
      public final void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
          
          // 检查是否已经注册
          // ...
           if (doConnect(remoteAddress, localAddress)) {
               // 连接操作已完成
               // 1.触发 active 事件
               fulfillConnectPromise(promise, wasActive);
           } else {
               // 失败重连
               int connectTimeoutMillis = config().getConnectTimeoutMillis();
               if (connectTimeoutMillis > 0) {
                   connectTimeoutFuture = eventLoop().schedule(new Runnable() {
                       @Override
                       public void run() {
                           if (connectPromise != null && 
                               // 唤醒所有监听器
                               connectPromise.tryFailure(cause)) {
                               close(voidPromise());
                           }
                       }
                   }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
               }
               
               // 取消连接 / 同时取消重连任务
           }
      }
      
      // 完成连接操作，Nio 关注 OP_CONNECT
      public final void finishConnect() {
           doFinishConnect();
           fulfillConnectPromise(promise, wasActive);
      }
      
      // Nio 关注 OP_WRITE 事件
      public final void forceFlush() {         
          super.flush0();
      }
      
      protected final void flush0() {
          if (!isFlushPending()) {
              super.flush0();
          }
      }
      
      // TODO: 为什么要设置关注 NIO OP_WRITE 事件呢？（猜测：依据功能来看，是希望能够判断通道是否正在 flush，1.设置一个状态位，当通道正在 flush 时，设置状态位，那么显然就需要关注 OP_WRITE，因为只有当出现 OP_WRITE 时通道才是 flush，write 只是将数据放入缓冲区，flush时设置 关心OP_WRITE事件，完成后取消关心OP_WRITE事件，如果通道一直关注 OP_WRITE，而一般情况下通道都可写，那么将不断从select()方法返回从而导致死循环）但是有一个疑问：OP_WRITE 到底是代表 Channel 的状态是可写的，还是实际有数据在写入 Socket 中，如果是前者，猜测是否能够合理解释，但是如果是后者
      
      // 通过查询资料以及查看源码注释：写操作的就绪条件为底层缓冲区有空闲空间，而写缓冲区绝大部分时间都是有空闲空间的，所以当你注册写事件后，写操作一直是就绪的，选择处理线程全占用整个CPU资源。所以，只有当你确实有数据要写时再注册写操作，并在写完以后马上取消注册（在 windows 下经过测试 NIO 默认也是水平触发，写操作只要关注了就会导致 selector 一直返回）
      
      // 前置知识：epoll 的水平触发(level trigger，LT)和边缘触发(edge trigger，ET)
      // LT:
      // 1.对于读操作：只要缓冲内容不为空，LT模式返回读就绪
      // 2.对于写操作：只要缓冲区还不满，LT模式会返回写就绪
      
      // ET:
      // 1.对于读操作：（1）当缓冲区由不可读变为可读的时候，即缓冲区由空变为不空的时候
      //			   （2）当有新数据到达时，即缓冲区中的待读数据变多的时候
      // 			   （3）
      
      // 2.对于写操作：（1）当缓冲区由不可写变为可写时
      // 			   （2）当有旧数据被发送走，即缓冲区中的内容变少的时候
      // 			   （3）
      
      // 那么最后，两种方式分别适应什么业务场景？
      
      // 1.这个 Channel 注册了 OP_WRITE 
      // 2.如果关注了 OP_WRITE 则等会再写，否则直接写
      // 3.为 true 表示正在 Flush
      private boolean isFlushPending() {
          SelectionKey selectionKey = selectionKey();
          return selectionKey.isValid() && 
              (selectionKey.interestOps() & SelectionKey.OP_WRITE) != 0;
      }
      ```
      

  - AbstractNioMessageChannel（服务端）

    - 作用：该类主要完善flush事件框架的doWrite细节和实现read事件框架，是底层数据为消息的NioChannel，服务端Accept的一个Channel被认为是一条消息，UDP数据报也是一条消息
    
    - 代码：
    
      ```java
      /******************** AbstractNioMessageChannel **************/
      
      /******************** NioMessageUnsafe *********************/
      @Override
      public void read() {
      	RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
      	// 1.获取接受到的消息个数（NIO 中一般为 1或者 0 表示接受到的连接数）
          
          // 2.触发 pipeline.fireChannelRead 事件（注意：这里 OP_ACCEPT 也是看作一种读事件处理）
          // 模板方法，读取消息（tcp nio 连接可以查看 NioServerSocketChannel）
          do{
              int localRead = doReadMessages(readBuf);
              
      		 doReadMessages(readBuf);
              // 达到最大可读数，可以通过配置设置
              if (readBuf.size() >= maxMessagesPerRead) { 
                  break;
              }
              
              allocHandle.incMessagesRead(localRead);
              
          }while(allocHandle.continueReading()) //读缓冲区还有数据
         
          
          // 3.触发 pipeline.fireChannelReadComplete()
      }
      
      
      
      /************************ RecvByteBufAllocator ******************/
      /**
      * 首先从注释中可以看出这个接口的作用就是分配一个足够大的 buffer 可以容纳所有的 inbound data
      * 但是又不能太大，否则会浪费空间，太小又会频繁内存复制 「数据接收缓冲区分配器」
      * 主要解决问题是：SocketChannel#read(ByteBuffer) 时，无法确定创建多大的 ByteBuffer，因 * 为使用 read时，并
      */
      
      /**
      *  实际进行操作的对象，具有统计接受到的消息的大小
      */
      Handle newHandle();
      
      /**
      *
      * 创建一个ByteBuf 作为缓冲区
      */
      ByteBuf allocate(ByteBufAllocator alloc);
      
      /**
      *
      * 增加已读的消息数量
      */
      void incMessagesRead(int numMessages);
      
      /**
      *
      * 获取上一次读取的字节数
      */
      int lastBytesRead();
      
      /**
      *
      * 是否还能继续读取
      */
      boolean continueReading();
      
      /**
      *
      * 读取完成
      */
      void readComplete();
      
      // 委托类，对 Handler 做增强
      /**************** DelegatingHandle ******************/
      
      /**
      * 前置知识：selector 与 OP_READ
      *
      * 监听OP_READ事件的Channel，Selector判断的其实就是Channel的有效可读字节数（系统缓冲区？），对于有数据可读的Channel，如果你数据没有读完，下次select()多路复用器依然会再返回它。所以，Netty会进行循环读，但是由于不知道对端会发送多少数据，所以就需要对读取次数做限制，防止一直读取数据，阻塞了整个 event loop
      */
      // 在 event loop 中限制读循环的次数，这应该是对服务端做限制的（message）
      /**************** MaxMessagesRecvByteBufAllocator ***************/
      /**
      *
      * event loop 读取消息（也可以看做一次循环可以读取的最大连接数，默认是16）的最大数量限制
      */
      int maxMessagesPerRead();
      
      // 限制读操作时，读取的字节最大数量
      /*************** MaxBytesRecvByteBufAllocator ******************/
      // 读取字节限制
      int maxBytesPerRead();
      
      /*************** DefaultMaxMessagesRecvByteBufAllocator ***********/
      // 最大读取消息数量
      private volatile int maxMessagesPerRead;
      
      /**
      是否关心 还有更多的数据可读？
      如果为 false，则无条件认为还有数据可读，直到下次循环读取到0字节为止。
      这可能会导致多执行一次无效读，无意义的创建一个ByteBuf。
      如果为 true，则是认为已经没有数据可读（根据源代码来的）
       */
      private volatile boolean respectMaybeMoreData = true;
      
      /************** MaxMessageHandle ********************/
      // 最大读取多少次消息，默认16次，没读完，下次select接着读。
      private int maxMessagePerRead;
      // 读取的总消息数
      private int totalMessages;
      // 读取的字节总数
      private int totalBytesRead;
      // 尝试读取的字节数，默认是ByteBuf的可写字节数，即尽量把ByteBuf填满。
      private int attemptedBytesRead;
      // 上次读取的字节数，根据它调整下次分配的缓冲区大小。
      private int lastBytesRead;
      
      /**
       如果本次尝试读取的字节数等于上次读取的字节数，说明可能还有更多的数据需要多，因为每次相等的话
      */
      private final UncheckedBooleanSupplier defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
          @Override
          public boolean get() {
              return attemptedBytesRead == lastBytesRead;
          }
      };
      
      public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier){
          // autoRead 后面再研究
          return config.isAutoRead() &&
              // 是否关心还有更多数据以及通过上次读取的字节数来判断是否还有更多的数据可读
              (!respectMaybeMoreData || maybeMoreDataSupplier.get()) &&
              // 没有超过读取最大次数
              totalMessages < maxMessagePerRead &&
              // 没有关闭流/出现异常
              totalBytesRead > 0;
      }
      
      /**************** AdaptiveRecvByteBufAllocator ****************/
      // 可以自适应的分配 buf 大小，防止出现上述情况
      
      ```
    
  - NioServerSocketChannel（服务端）

    - 作用：AbstractNioChannel拥有NIO的Channel，具备NIO的注册、连接等功能。但I/O的读/写交给了其子类，又分为服务端处理和客户端处理

    - 代码：

      ```java
      /****************** NioServerSocketChannel *****************/
      // NioServerSocketChannel只支持 bind、read 和close 操作（read 可以看作 accept）
      // TODO: 这里为什么不是 SelectionKey.OP_ACCEPT | SelectionKey.OP_READ
      public NioServerSocketChannel(ServerSocketChannel channel) {
          super(null, channel, SelectionKey.OP_ACCEPT);
      }
      
      @Override
      protected int doReadMessages(List<Object> buf) throws Exception {
          // 接受连接
          SocketChannel ch = SocketUtils.accept(javaChannel());
          // 连接成功则加入消息集合
          if (ch != null) {
              buf.add(new NioSocketChannel(this, ch));
              return 1;
          }
          return 0;
      }
      
      // 因为NioServerSocketChannel不支持connect操作（时刻记住 ServerSocket 的作用）
      @Override
      protected boolean doConnect(
          SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
          throw new UnsupportedOperationException();
      }
      ```

  - AbstractNioByteChannel （客户端）

    - 作用：处理发送消息
    - 问题：什么时候可以直接用 POJO ? 什么时候又只能使用 ByteBuf 和 FileRegion ? 结合项目中出现的问题，当直接使用 POJO 对象时会报错？

    - 代码：

      ```java
      /********************** NioByteUnsafe *****************************/
       @Override
      public final void read() {
       
          do {
              // 通过自适应缓冲区分配器分配一个 ByteBuf
              byteBuf = allocHandle.allocate(allocator);
              // 设置上一次读取的字节数，为下一次的分配做准备
              // #doReadBytes 返回读取的字节数
              // 这里也可以猜测，操作系统必须是默认 LT 才可以如此实现，否则会造成数据接受的延迟
              allocHandle.lastBytesRead(doReadBytes(byteBuf));
              // 说明已经读完了
              if (allocHandle.lastBytesRead() <= 0) {
                  // 释放 ByteBuffer
                  byteBuf.release();
                  byteBuf = null;
                  close = allocHandle.lastBytesRead() < 0;
                  if (close) {
                      // There is nothing left to read as we received an EOF.
                      readPending = false;
                  }
                  break;
              }
      		// 读取次数（消息）加一
              allocHandle.incMessagesRead(1);
              readPending = false;
              // 触发 pipeline 读事件，用户去决定下一步如何处理
              pipeline.fireChannelRead(byteBuf);
              byteBuf = null;
          } while (allocHandle.continueReading());
      
          pipeline.fireChannelReadComplete();
      
          if (close) {
              closeOnRead(pipeline);
          }
      
          finally {
              // Check if there is a readPending which was not processed yet.
              // This could be for two reasons:
              // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
              // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
              //
              // See https://github.com/netty/netty/issues/2254
              if (!readPending && !config.isAutoRead()) {
                  removeReadOp();
              }
          }
      }
      
      /********************** AbstractNioByteChannel ********************/
      protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
          super(parent, ch, SelectionKey.OP_READ);
      }
      
      // 刷新发送缓存链表中的数据，由于write的数据没有直接写在Socket中，而是写在了ChannelOutboundBuffer缓存中，所以当调用flush()方法时，会把数据写入Socket中并向网络中发送
      private final Runnable flushTask = new Runnable() {
          @Override
          public void run() {
              ((AbstractNioUnsafe) unsafe()).flush0();
          }
      };
      
      /**
      * 只支持 ByteBuf和FileRegion
      */
      @Override
      protected final Object filterOutboundMessage(Object msg) {
          // 判断是否为 ByteBuf
      	if (msg instanceof ByteBuf) {
              // 判断是否为 directBuf 如果不为则转成 directBuf
          }
      
          if (msg instanceof FileRegion) {}
          
      	throw new UnsupportedOperationException();
      }
      
      
      /******************* NioSocketChannel *******************/
      @Override
      protected int doReadBytes(ByteBuf byteBuf) throws Exception {
          // 设置本次尝试读取的字节数，一般是会尽量将分配的 ByteBuf 填满
          allocHandle.attemptedBytesRead(byteBuf.writableBytes());
          // 调用 jdk 的底层方法，从系统缓冲区中读取数据到 byteBuf
          // 从注释中也可以看出，ByteBuf 是支持扩容的，这也是优于 ByteBuffer 的部分
          return byteBuf.writeBytes(javaChannel(), allocHandle.attemptedBytesRead());
      }
      ```
      
      

- ByteBuf

  - 问题：

    1.如何使用？

    2.内存如何分配？

    3.内存如何管理？

    4.线程是否安全？

    5.其他注意事项

  - 类结构

    ![](C:\Users\guangyong.deng\Desktop\博客\Netty\ByteBuf.png)

    ```java
    /*************************** ByteBuf ************************/
    
    // 1.读写方法
    ...
    // 2.创建 ByteBuf，分配内存，返回一个分配器
    public abstract ByteBufAllocator alloc();
    
    // 3.复制一个独立的 ByteBuf 两个 ByteBuf 相互不影响
    public abstract ByteBuf copy();
    
    // 4.复制一个共享内存的 ByteBuf
    public abstract ByteBuf duplicate();
    
    // 5.内存释放的方法，继承 ReferenceCounted
    
    /********************** AbstractByteBuf ******************/
    
    // 1.骨架设计，类似于 Collection，使用一个抽象类实现大部分公共方法
    
    // 2.初始化 静态代码块
    static {
    
        // 加载一些系统变量
    }
    
    // 内存泄漏检测（后面再看）
    static final ResourceLeakDetector<ByteBuf> leakDetector =
                ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ByteBuf.class);
    
    // 方法太多了，看几个感兴趣的方法
    // 1. 返回一个只读的 ByteBuf
    @Override
    public ByteBuf asReadOnly() {
        // 由子类实现（原有设计已被废除，某个子类重写）
    }
    
    @Override
    public ByteBuf copy() {
        // 由子类实现
        return copy(readerIndex, readableBytes());
    }
    
    @Override
    public ByteBuf duplicate() {
        // 构造一个子类实现（原有设计已被废除，某个子类重写）
        return new UnpooledDuplicatedByteBuf(this);
    }
    ```

    

  - 使用方式

    ```java
    ByteBuf byteBuf;
    
    // 使用堆内存（默认使用非池化对象）
    byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
    // 使用直接内存
    byteBuf = ByteBufAllocator.DEFAULT.directBuffer();
    
    // 非池化对象
    byteBuf = Unpooled.buffer();
    
    // 池化对象（4.1 默认使用）
    
    // CompositeByteBuf
    ```

  - 内存如何分配

    - ByteBuf 数据结构

      ```
      +-------------------+------------------+------------------+
      *      | discardable bytes |  readable bytes  |  writable bytes  |
      *      |                   |     (CONTENT)    |                  |
      *      +-------------------+------------------+------------------+
      *      |                   |                  |                  |
      *      0      <=      readerIndex   <=   writerIndex    <=    capacity
      ```

    - 分配器：

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\ByteBufAllocator.png)

      ```java
      /******************** ByteBufAllocator *****************/
      // 1.分配 buffer ，应该是线程安全的
      
      // 默认分配器，如果是安卓平台，默认分配 unpooled，否则分配 pooled 
      // 至于池化对象是 heap 还是 direct，有两个决定因素
      // 一：可以在系统变量中设置 "noPreferDirect" 为 false （默认）
      // 二： CLEANER 不能为 NOOP（空）JDK 版本高于 6，支持 direct 会有默认的实现方式
      // 三：CLEANER != NOOP && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
      // 所以综合所述，默认的分配器是 池化 直接内存
      ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;
      
      // 2.创建 byteBuf 的方法 
      
      ByteBuf buffer();
      
      ByteBuf ioBuffer();
      
      // ...
      
      /******************* AbstractByteBufAllocator *******************/
      // 默认使用 heap
      protected AbstractByteBufAllocator(boolean preferDirect) {
          directByDefault = preferDirect && PlatformDependent.hasUnsafe();
          emptyBuf = new EmptyByteBuf(this);
      }
      
      // 子类实现
      protected abstract ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity);
      
      protected abstract ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity);
      
      
      /****************** PooledByteBufAllocator *******************/
      
      // 默认堆内存类型PoolArena个数
      private static final int DEFAULT_NUM_HEAP_ARENA;
      
      // 默认直接内存类型PoolArena个数
      private static final int DEFAULT_NUM_DIRECT_ARENA;
      
      
      static {
          
          // 默认 arena 数：cpu核心线程数与最大堆内存/2/(3*chunkSize)这两个数中的较小者
          // 
          DEFAULT_NUM_HEAP_ARENA = Math.max(0,
                  SystemPropertyUtil.getInt(
                          "io.netty.allocator.numHeapArenas",
                          (int) Math.min(
                                  defaultMinNumArena,
                                  runtime.maxMemory() / defaultChunkSize / 2 / 3)));
      
      }
      ```
    
      1. 以 HeapByteBuf 为例，如何将真实的内存与分配算法结合起来进行管理？
    
         - 创建 heapBuf，默认调用 PooledByteBufAllocator 进行创建
         - 首先尝试通过 ThreadCache 的 Arena 分配
         - 创建 PooledHeapBuf（或者从对象池中复用对象，注意此时实际内存还没有分配，Arena 进行内存分配）
         - 根据请求内存的大小，以及是否有 cache 进行不同类型的分配（假设为没有内存，Page 级别的请求）
    
      2. 内存管理是对整个虚拟机内存进行管理？还是对创建的 ByteBuf 进行管理？
    
         - 以 heap 为例，用户每次创建 ByteBuf，都是通过 ByteBufAllocator.DEFAULT （全局唯一实例）来进行内存分配，所以管理的是 虚拟机内存，会将内存按层次分配好，创建 ByteBuf 时需要的内存即会从不同层次当中获取
    
      3. 此内存管理的作用是什么？
    
         - Slab 和 buddy 分配算法的好处
    
      4. 为什么会是先创建 ByteBuf ，再是内存分配？
    
         - 从对象池中回收 ByteBuf 时，里面是没有分配好内存的，此时是无法使用的（空架子）
    
         - 使用完内存分配算法之后，才会创建 byte[] 数组（代码在哪里？：以 heapBuf 为例？可以看到 memory 数组大小正好是一个 chunk 的大小）
    
           ![](C:\Users\guangyong.deng\Desktop\博客\Netty\chunk_init.png)
    
           ![](C:\Users\guangyong.deng\Desktop\博客\Netty\chunk_init_2.png)
    
      5. direct 分配与 heap 有什么区别？
    
      6. pooled 与 unpooled 又有什么区别？
    
         
    
    - 实际容器-字节数组
    
      - HeapByteBuf（创建的过程中还使用了缓存机制，后面再看）
    
        ```java
        /********UnpooledHeapByteBuf***********/
        // 分配器（todo:作用是什么？）
        private final ByteBufAllocator alloc;
        // 底层字节数组
        byte[] array;
        // nio 的 ByteBuffer
        private ByteBuffer tmpNioBuf;
        
        public UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
             setArray(allocateArray(initialCapacity));
             setIndex(0, 0);
        }
        // 最终可以发现最后使用的是字节数组
        protected byte[] allocateArray(int initialCapacity) {
            return new byte[initialCapacity];
        }
        ```
      
      - DirectByteBuf（关于堆外内存的一些知识请查看 Nio ByteBuffer）
      
        ```java
        /********PooledUnsafeDirectByteBuf*****/
        static PooledUnsafeDirectByteBuf newInstance(int maxCapacity) {
            // 会通过对象池创建堆外内存引用
            PooledUnsafeDirectByteBuf buf = RECYCLER.get();
            // 格式化
            buf.reuse(maxCapacity);
            return buf;
        }
        ```
      
    
  - 内存如何管理

    - 引用计数
    
      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\reference.png)
    
    - 源码解读
    
      ```java
      /**************** AbstractReferenceCountedByteBuf *********************/
      // 内部引用计数
      private volatile int refCnt;
      
      // 释放资源抽象接口
      protected abstract void deallocate();
      
      /**************** ReferenceCountUpdater ******************************/
      
      /**
      * 注意逻辑实现计数器类
      * netty 使用了奇数与偶数来表示引用
      * 1.偶数，则表示存在引用，同理如果引用一次增加 2，此时真实引用为 1，内部引用计数为 2，真实引用等于内部引用计数无符号右移一位
      * 2.奇数，则表示不存在引用，减少一次减2，当释放的内部引用计数为 2后，会设置为为 1，此时真实引用即等于 1 >>> 1 等于 0
      * 
      */
      // 通过原子变量操作引用计数，保证线程安全
      protected abstract AtomicIntegerFieldUpdater<T> updater();
      
      // 获取真实的计数
      private static int realRefCnt(int rawCnt) {
          // 大部分情况下，真实引用也就 1，2 所以先做判断
          // 如果是奇数直接返回 0
          return rawCnt != 2 
              && rawCnt != 4 
              && (rawCnt & 1) != 0 ? 0 : rawCnt >>> 1;
      }
      
      // 获取真实计数
      public final int refCnt(T instance) {
          return realRefCnt(updater().get(instance));
      }
      
      ```
    
      1. netty 是如何保存对象的引用计数的？
         - netty 是通过继承了 AbstractReferenceCountedByteBuf 来实现引用计数，即对应的 ByteBuf 类可以实现引用计数，普通对象无法计数
      2. AtomicIntegerFieldUpdater 相较于 AtomicInteger 的好处在哪里？
         - 内存使用少的多。创建 100 个 ByteBuf 如果使用 AtomicInteger 就需要同步创建 一百个，但是使用 Update 只需要创建一个
    
      3. netty 是如何解决循环依赖的？
    
      4. 什么时候？谁来释放？
    
         在 Handle 链中谁是最后使用者，谁负责释放
    
         - 入站
           - 对原消息不做处理，调用 ctx.fireChannelRead(msg)把原消息往下传，那不用做什么释放
           - 将原消息转化为新的消息并调用 ctx.fireChannelRead(newMsg)往下传，那必须把原消息release掉
           - 如果已经不再调用ctx.fireChannelRead(msg)传递任何消息，那更要把原消息release掉
           - 假设每一个Handler都把消息往下传，Handler并也不知道谁是启动Netty时所设定的Handler链的最后一员，所以Netty在Handler链的最末补了一个TailHandler，如果此时消息仍然是ReferenceCounted类型就会被release掉
         - 出战
           - 同上，最终由 HeadHandler 处理
         - 异常处理
           - 异常处理释放
    
  - 内存泄漏检测

    - 源码

      ```java
      /****************** ResourceLeakDetector ******************/
      
      private static final Level DEFAULT_LEVEL = Level.SIMPLE;
      
      // -Dio.netty.leakDetectionLevel=[检测级别]
      
       public enum Level {
              /**
               * 完全禁止泄露检测，省点消耗
               */
              DISABLED,
              /**
               * 默认等级，告诉我们取样的1%的ByteBuf是否发生了泄露，但总共一次只打印一次，看不到就没有了
               * 
               */
              SIMPLE,
              /**
               * 告诉我们取样的1%的ByteBuf发生泄露的地方。每种类型的泄漏(创建的地方与访问路径一致)只打印一次。对性能有影响
               */
              ADVANCED,
              /**
               * 跟高级选项类似，但此选项检测所有ByteBuf，而不仅仅是取样的那1%。对性能有绝大的影响
               */
              PARANOID;
      
       }
      
      /**
      * 首先明确整个内存泄漏所需要完成的任务
      *	1. netty 使用了池化操作减轻对创建 IO Buffer 的代价，需要对池化对象进行管理，同步引入了引用计数，但在 JVM 中本身存在 GC，如何进行相互配合，就成了问题
      	2. 某些情况下，当 ByteBuf 在不可达时，还未 release 就被 GC 回收，此时该 ByteBuf 不可用，但是所占用的对象池/内存池资源也没有还回去，就会造成内存泄漏，因此就需要两个功能来完成配合
      		2.1 在泄漏发生的时候，我们需要有个通知机制来知晓被GC的对象没有调用自己该有的release方法来释放池化资源
      		2.2 需要有个记录机制来记录这些泄漏的对象使用的地方，方便溯源
      		TODO：关于 ByteBuf GC 为何对象池资源无法释放？具体的逻辑又是怎样的？
      			1.ByteBuf 虽然也持有一些其他如 arena 的引用，但是在可达性算法中，是使用的有向图，即必须从根节点达到对象引用，所以持有 arena 对象引用并不影响回收
      			2.同理，一旦 ByteBuf 被 GC，无法 release，占用的比如 arena 资源就无法正常释放，也无法使用，就造成内存泄漏
      			3.同样，要实现上述功能，最容易想到的是
      				3.1 在 GC 时，进行是否 release 检测，比如设置一个状态标志位之类的，在 GC 时则需要检查这个标志位就行了
      				3.2 怎么判断一个对象引用在被 GC，就正好可以利用几种引用，将 ByteBuf 设置（包装）为 WeakReference（查看引用知识），在 ReferenceQueue 中检查对象的标志位即可
      				 
      *
      * 鉴于内存泄漏比较复杂，这里就以 Handler 中未正常 release 为例，查看如何检测
      *
      *	1.默认是 Simple 级别，通过 allocator 创建 ByteBuf buf，并调用 PooledByteBufAllocator#toLeakAwareBuffer(buf)
      *
      *		1.1 根据设置级别，创建 ResourceLeakTracker leak
      *		1.2 并根据 AbstractByteBuf#ResourceLeakDetector#track 检测，在这个版本中，会检查大概 1/128 的 byteBuf（通过生成一个 0~127 的随机数，并判断这个随机数是否为 0）
      		1.3 会将创建的 leak 加入 ResourceLeakDetector 的 allLeaks 里面，当 byteBuf 进行 GC 时，leak 会加入 referenceQueue 中
      		1.4 检查以前的 leak 是否有内存泄漏，leak 出队列，设置 byteBuf 为 null，并判断是否能够从 allLeaks 删除 当前 leak
      *	
      *	2.当释放资源时，会调用 AbstractReferenceCountedByteBuf#release 进行计数减一，并判断是否进行资源回收，进行 deallocate() 方法
      *		2.1 在进行内存泄漏时会使用 SimpleLeakAwareByteBuf#closeLeak -> leak.close(obj) -> leak.remove(this)
      *		总体来说：和最初思路一致，如果 GC 时可以成功 remove，则可以说明没有 release，因为如果 release 了，会将 leak remove 了
      *				最后也就是说为什么需要使用一个 set 来作为标志位，而不是 AtomicBoolean ？
      *
      *	3.PooledByteBuf 进行资源回收，会释放各种资源，以及将 PooledByteBuf 对象返回对象池，进行下一次复用
      *
      *	4.netty 自定义的内存池（arena, chunk, subpage ... 释放）
      */
      
      private final Set<DefaultResourceLeak<?>> allLeaks =
                  Collections.newSetFromMap(new ConcurrentHashMap<DefaultResourceLeak<?>, Boolean>());
      
      
      
      /*************************** SimpleLeakAwareByteBuf ***********************/
      // 内存泄漏器（抽样）追踪的 ByteBuf
      private final ByteBuf trackedByteBuf;
      // 内存泄漏器
      final ResourceLeakTracker<ByteBuf> leak;
      
      
      
      /*************************** AbstractByteBufAllocator *******************/
      
      static {
          ResourceLeakDetector.addExclusions(AbstractByteBufAllocator.class, "toLeakAwareBuffer");
      }
      ```
      
      1. 哪些内存需要内存手动回收？即哪些内存可能产生内存泄漏？
         - Pooled 无论是使用 heap 还是 direct 这个好理解
         - Unpooled 使用 direct （猜测：仍然使用的内存池？），heap 不会（直接 使用的字节数组，由 GC 负责）
      2. 如何回收？
      3. 哪些情况下可能产生内存泄漏？
      4. 一些衍生问题，JDK 内存泄漏检测工具？Java 如何检测是否内存泄漏？
      
    - JVM 内存回收的一些思考
    
      ```java
      // -Xmx20m
      public class LeakMemory {
      
          private static final ObjB objB = new ObjB();
      
          void test() {
              ObjA objA = new ObjA(objB);
          }
      
          public static void main(String[] args) {
      		// 没有 oom 发生
              for (int i = 0; i < 100000; i++) {
                   new LeakMemory().test();
              }
          }
      }
      ```
    
      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\available-graphic.png)
    
      1. 从上面的可达性图中可以看出，虽然 ObjA 可达 ObjB，但是无法从 ObjB 可达 ObjA，所以 ObjA 相对于 main 来说仍是不可达，仍是可以回收的，如果 ObjB 也可达 ObjA 则无法回收，发生内存泄漏
    
      2. 1）**可达状态**：在一个对象创建后，有一个以上的引用变量引用它。在有向图中可以从起始顶点导航到该对象，那它就处于可达状态
    
         2）**可恢复状态**：如果程序中某个对象不再有任何的引用变量引用它，它将先进入可恢复状态，此时从有向图的起始顶点不能再导航到该对象。在这个状态下，系统的垃圾回收机制准备回收该对象的所占用的内存，在回收之前，系统会调用finalize()方法进行资源清理，如果资源整理后重新让一个以上引用变量引用该对象，则这个对象会再次变为可达状态；否则就会进入不可达状态（待考证）
    
         3）**不可达状态**：当对象的所有关联都被切断，且系统调用finalize()方法进行资源清理后依旧没有使该对象变为可达状态，则这个对象将永久性失去引用并且变成不可达状态，系统才会真正的去回收该对象所占用的资源

- Handler

  - 编解码器

    - 思路：由于 tcp 会产生拆包问题（什么是拆包查看网络协议），所以一次发送读取到的数据不一定是正确，所以每次读取缓冲区数据

      1. 必定有方式可以保留处理不完的ByteBuf
      2. 必定有方式将处理不完的ByteBuf和当前的ByteBuf拼接到一起

    - 源码

      ```java
      /****************** Cumulator *******************************/
      
      // 类加器接口，即上面所说的处理消息的方式，可能一次处理不完，需要保存待下一次处理
      ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in);
      
      /******************* ByteToMessageDecoder ********************/
      
      // 将新读取到的数据添加到累加缓冲区中
      public static final Cumulator MERGE_CUMULATOR = ...
        
      // 差不多，只不过使用 CompositeByte 进行拼装
      public static final Cumulator COMPOSITE_CUMULATOR = ...
      
      // 初始状态
      private static final byte STATE_INIT = 0;
      // 正在调用子类解码
      private static final byte STATE_CALLING_CHILD_DECODE = 1;
      // 处理器待删除
      private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
      
      // 累加缓冲区
      ByteBuf cumulation;
      // 默认是合并累积
      private Cumulator cumulator = MERGE_CUMULATOR;
      // 是否只解码一次
      private boolean singleDecode;
      // 是否是第一次累加缓冲区
      private boolean first;
      
      // 状态
      private byte decodeState = STATE_INIT;
      // 读取16个字节后丢弃已读的
      private int discardAfterReads = 16;
      // cumulation读取数据的次数
      private int numReads;
      
      
      // 子类实现具体的解码方式
      protected abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
      
      
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
          if (msg instanceof ByteBuf) {
              // 保存解码后的消息
              CodecOutputList out = CodecOutputList.newInstance();
              try {
                  // 第一次累加标志位
                  first = cumulation == null;
                  // 如果是第一次累加，则创建一个空的累加缓冲区
                  cumulation = cumulator.cumulate(ctx.alloc(),
                                                  first ? Unpooled.EMPTY_BUFFER : cumulation, (ByteBuf) msg);
                  callDecode(ctx, cumulation, out);
              } finally {     
                   
                  // 判断累加器的状态
                  ...
                  // 读取数据的次数大于阈值，则尝试丢弃已读的，避免占着内存
                   if (++numReads >= discardAfterReads) {
                      numReads = 0;
                      discardSomeReadBytes();
                  }
      
                  int size = out.size();
                  firedChannelRead |= out.insertSinceRecycled();
                  // 尝试传递数据
                  fireChannelRead(ctx, out, size);
              }
          } else {
              ctx.fireChannelRead(msg);
          }
      }
      
      protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
         
          // 只要累加缓冲区有数据则尝试解码
          while (in.isReadable()) {
              // 已解码的消息数量
              final int outSize = out.size();
              // 有解码好的数据则传递给后方
              if (outSize > 0) {
                  fireChannelRead(ctx, out, outSize);
                  out.clear();
                  // 上下文被删除了就不处理了
                  if (ctx.isRemoved()) {
                      break;
                  }
              }
              // 还有多少数据
              int oldInputLength = in.readableBytes();
              // 解码
              decodeRemovalReentryProtection(ctx, in, out);
      
              // 没有生成新的消息，可能要求不够无法解码出一个消息
              if (out.isEmpty()) {
                  if (oldInputLength == in.readableBytes()) {
                      break;
                  } else {
                      continue;
                  }
              }
      
              // 解码器没有读取数据，抛出异常
          }
      }
      
      final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
                  throws Exception {
          decodeState = STATE_CALLING_CHILD_DECODE;
          try {
              // 调用子类解码
              decode(ctx, in, out);
          } finally {
              // 考虑处理器被删除了怎么处理
          }
      }
      ```

      

  - IdleStateHandler

    作用：心跳检测

    ```java
    /**************** IdleStateHandler *********************/
    // 读超时时间
    private final long readerIdleTimeNanos;
    // 写超时时间
    private final long writerIdleTimeNanos;
    // 写超时时间
    private final long allIdleTimeNanos;
    
    
    ```

    

- 一些数据结构或者工具

  - FastThreadLocal

    - 类结构

    ![](C:\Users\guangyong.deng\Desktop\博客\Netty\InternalThreadLocalMap.png)

    - 用途

      - 作为线程本身的独有内存，防止多线程竞争，以及上下文竞争

    - 与 ThreadLocal 的区别

      - ThreadLocal 实现原理

        - 每个 thread 中都有一个 ThreadMap 属性，会在开始使用时才创建（延迟加载），如何实现创建多个 ThreadLocal,不会产生多个 ThreadMap （会进行判断，只有当线程不存在 ThreadMap 时才会创建）
          key 为 ThreadLocal 相当与 多个线程都会有一个指向 ThreadLocal的弱引用,所以只要有一个 线程不释放 ThreadLocal，都不会被释放

          思考：为什么 ThreadLocalMap 会被定义为 static
                      1.创建静态内部类时不需要将静态内部类的实例绑定在外部类的实例上，可以直接通过 类名.访问
                       2.ThreadLocalMap 中有 static 属性，所以作为内部类必须被声明为 static

          ​			 3.它的操作仅限于ThreadLocal类中，不对外暴露

        - 如何降低哈希冲突

          ThreadLocalMap采用黄金分割数的方式，大大降低了哈希冲突的情况，可以神奇的保证nextHashCode生成的哈希值，均匀的分布在2的幂次方上，且小于2的32次方

          黄金分割数：近似于 黄金分割数（0.618）乘以2的32次方

          ```java
          private static final int HASH_INCREMENT = 0x61c88647;
          ```

        - 缺点 ：

          - 放入元素是通过 hash 算法，会出现 hash 冲突的情况，解决冲突的方式叫线性探测法，就是看冲突索引的下一个能不能放，不能就继续往后找，不够就扩容，直到能放下为止。所以一次放的操作就可能消耗很大了（为啥会使用线性探测法：1.使用了黄金分割数，出现冲突可能性较小 2.在冲突和扩容时都会清理掉 key 为 null 的过期节点）

          - 内存溢出（每次结束后调用 remove 操作）

            - 前置知识点学习 WeakReference

            - 为什么需要？
            
              ```java
              // 只要 obj 还指向 Object， obj::Object 对象就不会被回收
              Object obj = new Object();
              // Reference 置为空，方便回收
              // 一般来说，不需要手动置为空，随着方法的结束，引用出栈，没有引用指向，GC自动回收
              // 如果是类对象或者全局对象的回收（需要参考 JVM GC，这里不考虑）
              obj = null;
              
              // 但是, 也有特殊例外. 当使用cache的时候, 由于cache的对象正是程序运行需要的, 那么只要程序正在运行, cache中的引用就不会被GC
              // 那么随着cache中的reference越来越多, GC无法回收的object也越来越多, 无法被自动回收. 当这些object需要被回收时, 回收这些object的任务只有交给程序编写者了. 然而这却违背了GC的本质
              //  当一个对象仅仅被weak reference指向, 而没有任何其他strong reference指向的时候, 如果GC运行, 那么这个对象就会被回收(注意是 obj 被回收，而不是 weaObj 被回收)
              WeakReference<Object> weakObj = new WeakReference<>(obj);
              ```
            
            - 案例讲解
            
              ```java
              /**
              * 此方法结果就是运行一段时间后，会发现 car 实例对象已经被回收了
              */
              public static void weakReferenceDemo() {
                      Car car = new Car("一汽大众");
                      WeakReference<Car> weakReferenceCar = new WeakReference<>(car);
                      int i = 0;
                      while (true) {
                          if(weakReferenceCar.get()!=null){
                              i++;
                              // 这里如果将 weakReferenceCar 改为 car 对象就无法回收
                              System.out.println("Object is alive for "+ i + " loops - " + weakReferenceCar);
                          }else{
                              // 最终会执行到这里
                              // 疑惑？按理来说 Car 对象应该还存在一个强引用 car，不应该被回收（此处原文的解释是 car 在循环之外，已经没有使用的地方了，所以被优化了，有待研究）
                              System.out.println("Object has been collected.");
                              System.out.println("after collected: " + weakReferenceCar);
                              break;
                          }
                      }
                  }
              /**
              * 最后可以将 weakReferenceCar 加入到 ReferenceQueue 进行回收处理
              */
              ```
            
              ```java
              public class WeakReference<T> extends Reference<T> {
                  
                  /**
                   * 构造一个弱引用实例，此弱引用实例会引用给定的参数对象
                   */
                  public WeakReference(T referent) {
                      super(referent);
                  }
                  
              }
              ```
            
            - 扩展思考：为什么 ThreadLocalMap 中的 Entry 会使用 WeakReference（不使用这个会发送什么？）【便于回收 ThreadLocal 对象】
            
              ```java
              ThreadLocal local = new ThreadLocal();
              local.set("当前线程名称："+Thread.currentThread().getName());//将ThreadLocal作为key放入threadLocals.Entry中
              Thread t = Thread.currentThread();//注意断点看此时的threadLocals.Entry数组刚设置的referent是指向Local的，referent就是Entry中的key只是被WeakReference包装了一下
              local = null;//断开强引用，即断开local与referent的关联，但Entry中此时的referent还是指向Local的，为什么会这样，当引用传递设置为null时无法影响传递内的结果
              System.gc();//执行GC
              t = Thread.currentThread();//这时Entry中referent是null了，被GC掉了，因为Entry和key的关系是WeakReference，并且在没有其他强引用的情况下就被回收掉了
              //如果这里不采用WeakReference，即使local=null，那么也不会回收Entry的key，因为Entry和key是强关联
              //但是这里仅能做到回收key不能回收value，如果这个线程运行时间非常长，即使referent GC了，value持续不清空，就有内存溢出的风险
              //彻底回收最好调用remove
              //即：local.remove();//remove相当于把ThreadLocalMap里的这个元素干掉了，并没有把自己干掉
              System.out.println(local);
              ```
            
            案例讲解：
            
            ```java
            public static void main(String[] args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1000; i++) {
                            TestClass t = new TestClass(i);
                            t.printId();
                            // 会发生内存泄漏
                            t = null;
                            // t.threadLocal.remove(); 不会发生泄漏
                        }
                    }
                }).start();
            }
            
              static class TestClass{
                  private int id;
                  private int[] arr;
                  private ThreadLocal<TestClass> threadLocal;
                    TestClass(int id){
                        this.id = id;
                        arr = new int[1000000];
                        threadLocal = new ThreadLocal<>();
                        threadLocal.set(this);
                    }
            
                    public void printId(){
                        System.out.println(threadLocal.get().id);
                    }
                }
            }
            ```
            
            **leak**:
            
            ![](C:\Users\guangyong.deng\Desktop\博客\Netty\thread-local-memory-leak.png)
            
            循环一次内存状态
            
            > - t为创建TestClass对象返回的引用，临时变量，在一次for循环后就执行出栈了
            > - thread为创建Thread对象返回的引用，run方法在执行过程中，暂时不会执行出栈
            > - 调用t=null后，虽然无法再通过t访问内存地址，但是当前线程依旧存活，可以通过thread指向的内存地址，访问到Thread对象，从而访问到ThreadLocalMap对象，访问到value指向的内存空间，访问到arr指向的内存空间，从而导致Java垃圾回收并不会回收int[1000000]@541这一片空间
          
          **remove**:
          
          ![](C:\Users\guangyong.deng\Desktop\博客\Netty\thread-local-no-memory-leak.png)
          
          > 因为remove方法将referent和value都被设置为null，所以ThreadLocal@540和Memory$TestClass@538对应的内存地址都变成不可达，Java垃圾回收自然就会回收这片内存，从而不会出现内存泄漏的错误
      
      - 框架使用情况
      
        - Spring Security 使用 ThreadLocal 缓存用户信息
      
      - 流程
      
        ```java
        private static final FastThreadLocal<Integer> fastThreadId = new FastThreadLocal<>();
        private static final AtomicInteger nextId = new AtomicInteger(1);
            new FastThreadLocalThread(() -> {      
                fastThreadId.set(nextId.getAndIncrement());
                System.out.println("threadName=" + Thread.currentThread().getName() + ",threadId=" + fastThreadId.get());
                    }).start();
        ```
      
        1. 获取 当前线程中的 InternalThreadLocalMap
      
           ```java
           InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
           ```
      
        2. 根据当前线程是否为 FastThreadLocalThread 进行下一步
      
           2.1 如果是 FastThreadLocalThread 直接返回 InteranlThreadLocalMap
      
           2.2 如果不是则是 JDK 中的 Thread，则返回一个 ThreadLocal\<InternalThreadLocalMap>
      
           ​	  相当于即便是 JDK 中的 Thread 仍然使用的是 InteranlThreadLocalMap 只是再多包装了一层
      
        3. 获取当前 FastThreadLocal 中的 index 设置 value
      
           ```java
           threadLocalMap.setIndexedVariable(index, value)
           ```
      
           3.1 关于 index 的一些说明：
      
           ```java
           private final int index;
           
           public FastThreadLocal() {
               index = InternalThreadLocalMap.nextVariableIndex();
           }
           ```
      
           - index 如何包装线程安全：index 是通过一个原子变量来设置
      
           - 关于 index 与 FastThreadLocal 的关系：从代码中可以看出，每一次创建一个新的 FastThreadLocal 都会更新 index，即不同的 FastThreadLocal 对应不同的 index
      
             ![](C:\Users\guangyong.deng\Desktop\博客\Netty\FastThreadLocal.png)
      
        4. 插入元素，并判断是否需要扩容
      
      - 优点
      
        为了提升效率，采用了空间换时间的方式，采用数组来插入元素，而在数组下标0的位置放入一个 Set<FastThreadLocal<?>> set（具体效果未知）
      
      - 缺点：
      
        空间消耗大，内存消耗比较大，因为只会扩容，而且索引只会递增，这样数组就会越来越大。所以就是空间换时间了
      
        仍然存在内存泄漏问题
    
  - ChannelOutboundBuffer

    - 类结构

      

    - 用途：一个通道的出站缓冲区，所有要写的数据都会先存在这里，等到要刷新的时候才会真的写出去

    - 代码

      ```java
      private final Channel channel;
      
      // 即将被消费的开始节点
      private Entry flushedEntry;
      
      // 被添加的开始节点，但没有准备好被消费
      private Entry unflushedEntry;
      
      // 最后一个节点
      private Entry tailEntry;
      
      // 消息都是封装成内部的 Entry 类的
      // 存储结构是一个单链表
      static final class Entry {
      }
      ```
      
      ```java
      public void addMessage(Object msg, int size, ChannelPromise promise) {
           Entry entry = Entry.newInstance(msg, size, total(msg), promise);
          // ...
      }
      
      static Entry newInstance(...) {
          Entry entry = RECYCLER.get();
      }
      ```
      
      
      
      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\ChannelOutboundBuffer-addMessage.png)
      
      ```java
      // 这个方法并不是做刷新到 Socket 的操作。而是将 unflushedEntry 的引用转移到 flushedEntry 引用中
      // 为什么不直接刷新
      // 因为 Netty 提供了 promise，这个对象可以做取消操作，例如，不发送这个 ByteBuf 了，所以，在 write 之后，flush 之前需要告诉 promise 不能做取消操作了
      public void addFlush() {
           Entry entry = unflushedEntry;
              if (entry != null) {
                  // 防止多次调用 flush
                  if (flushedEntry == null) {
                      flushedEntry = entry;
                  }
                  do {
                      flushed ++;
                      // 通知 用户不能取消发送了
                      if (!entry.promise.setUncancellable()) {
                          // 如果失败，说明已经被取消，移除该节点
                          int pending = entry.cancel();
                          decrementPendingOutboundBytes(pending, false, true);
                      }
                      entry = entry.next;
                  } while (entry != null);
                  unflushedEntry = null;
              }
      }
      ```
      
    
  - Recycler

    - 类结构

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\Recycler.png)

    - 用途：轻量级对象池实现

    - 前置知识

      WeakHashMap（非线程安全的）：有自清理的机制，可以自动将只被弱引用的 key 自动回收掉，主要利用了 WeakReference 的特性
    
      某个WeakReference对象所指向的对象如果被判定为垃圾对象，Jvm会将该WeakReference对象放到一个ReferenceQueue里，我们只要看下这个Queue里的内容就知道某个对象还有没有用了
    
      ```java
      // ...
      private void expungeStaleEntries() {
              for (Object x; (x = queue.poll()) != null; ) {
                  synchronized (queue) {
                     	// 清理死数据
                  }
              }
          }
      ```
    
    - 代码
    
      Recycler 对象的创建
      
      ```java
      // 轻量级对象池实现
      public abstract class Recycler<T> {
           /**
           * 唯一ID生成器，用在两处：
           * 1、当前线程ID
           * 2、WeakOrderQueue的 id
           */
          private static final AtomicInteger ID_GENERATOR = new AtomicInteger(Integer.MIN_VALUE);
          
          /**
           * static变量, 生成并获取一个唯一id.
           * 用于pushNow()中的item.recycleId和item.lastRecycleId的设定
           */
          private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
          
          /**
           * 表示一个不需要回收的包装对象，用于在禁止使用Recycler功能时进行占位的功能
           * 仅当io.netty.recycler.maxCapacityPerThread<=0时用到
           */
          private static final Handle NOOP_HANDLE = new Handle() {
              @Override
              public void recycle(Object object) {
                  // NOOP
              }
          };
          
          /**
           * 每个Stack默认的最大容量
           * 注意：
           * 1、当io.netty.recycler.maxCapacityPerThread<=0时，禁用回收功能（在netty中，只有=0可以禁用，<0默认使用4k）
           * 2、Recycler中有且只有两个地方存储DefaultHandle对象（Stack和Link），
           * 最多可存储MAX_CAPACITY_PER_THREAD + 最大可共享容量 = 4k + 4k/2 = 6k
           *
           * 实际上，在netty中，Recycler提供了两种设置属性的方式
           * 第一种：-Dio.netty.recycler.ratio等jvm启动参数方式
           * 第二种：Recycler(int maxCapacityPerThread)构造器传入方式
           */
          private static final int MAX_CAPACITY_PER_THREAD = ...
          
          /**
           * 最大可共享的容量因子。
           * 最大可共享的容量 = maxCapacity / maxSharedCapacityFactor，maxSharedCapacityFactor默认为2
           */
          private static final int MAX_SHARED_CAPACITY_FACTOR = ...
          
          /**
           * 每个线程可拥有多少个WeakOrderQueue，默认为2*cpu核数
           * 实际上就是当前线程的Map<Stack<?>, WeakOrderQueue>的size最大值
           */
          private static final int MAX_DELAYE_DQUEUES_PERTHREAD = ...
          /**
           * WeakOrderQueue中的Link中的数组DefaultHandle<?>[] elements容量，默认为16，
           * 当一个Link中的DefaultHandle元素达到16个时，会新创建一个Link进行存储，这些Link组成链表，当然
           * 所有的Link加起来的容量要<=最大可共享容量。
           */
          private static final int LINK_CAPACITY = ...
          /**
           * 回收因子，默认为8。
           * 即默认每8个对象，允许回收一次，直接扔掉7个，可以让recycler的容量缓慢的增大，避免爆发式的请求
           */
          private static final int RATIO = ...
          /**
           * 1、每个Recycler类（而不是每一个Recycler对象）都有一个DELAYED_RECYCLED
           * 原因：可以根据一个Stack<T>对象唯一的找到一个WeakOrderQueue对象，所以此处不需要每个对象建立一个DELAYED_RECYCLED
           * 2、由于DELAYED_RECYCLED是一个类变量，所以需要包容多个T，此处泛型需要使用?
           * 3、WeakHashMap：当Stack没有强引用可达时，整个Entry{Stack<?>, WeakOrderQueue}都会加入相应的弱引用队列等待回收
           */
          private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED = ...
              
          /**
           * 1、每个Recycler对象都有一个threadLocal
           * 原因：因为一个Stack要指明存储的对象泛型T，而不同的Recycler<T>对象的T可能不同，所以此处的FastThreadLocal是对象级别
           * 2、每条线程都有一个Stack<T>对象
           */
          private final FastThreadLocal<Stack<T>> threadLocal = ...
             
          // 池中对象不够时，创建对象
          protected abstract T newObject(Handle<T> handle);
          
          // 所有的池化对象都被这个Handle包装，Handle是对象池管理的基本单位
          // 并通过一个 stack 存储 Handle
          public interface Handle<T> {
              // 回收对象
              void recycle(T object);
          }
          
      }
      ```
      
      **同线程获取对象**
      
      ```java
      /**
      * Recycler#get()
      * 获取对象
      */ 
      public final T get() {
          // 禁止使用回收功能
          if (maxCapacityPerThread == 0) {
              return newObject((Handle<T>) NOOP_HANDLE);
          }
          // 获取线程相关联的 stack
          // 当首次执行 threadLocal.get() 时，会调用 threadLocal#initialValue() 来创建一个 Stack 对象
          Stack<T> stack = threadLocal.get();
          // 池中是否有可用对象，如果有则直接返回，如果没有则新创建一个Handle
          DefaultHandle<T> handle = stack.pop();
          if (handle == null) {
              handle = stack.newHandle();
              // 创建一个新的池化对象并放入Handler的value中
              handle.value = newObject(handle);
          }
          return (T) handle.value;
      }
      ```
      
      ```java
      /**
      * Recycle#Stack
      */
      static final class Stack<T> {
      /**
       * 该Stack所属的线程
       * why WeakReference?
       * 假设该线程对象在外界已经没有强引用了，那么实际上该线程对象就可以被回收了。
       * 但是如果此处用的是强引用，那么虽然外界不再对该线程有强引用，但是该stack对象还持有强引用
       * （假设用户存储了DefaultHandle对象，然后一直不释放，而DefaultHandle对象又持有stack引用），导致该线程对象无法释放。
       */
          final WeakReference<Thread> threadRef;
              
      /**
       * Stack底层数据结构，真正的用来存储数据
       */
      	private DefaultHandle<T>[] elements;
      /**
       * elements中的元素个数，同时也可作为操作数组的下标
       * 数组只有elements.length来计算数组容量的函数，没有计算当前数组中的元素个数的函数，所以需要我们去记录，不然需要每次都去计算（TODO:会出现线程安全问题吗？）
       */
      	private int size;
      /**
       * elements最大的容量：默认最大为4k，4096
       */
      	private int maxCapacity;
              
      /**
       * 可用的共享内存大小，默认为maxCapacity/maxSharedCapacityFactor = 4k/2 = 2k = 2048
       * 假设当前的Stack是线程A的，则其他线程B~X等去回收线程A创建的对象时，可回收最多A创建的多少个对象
       * 注意：那么实际上线程A创建的对象最终最多可以被回收maxCapacity + availableSharedCapacity个，默认为6k个
       *
       * why AtomicInteger?
       * 当线程B和线程C同时创建线程A的WeakOrderQueue的时候，会同时分配内存，需要同时操作availableSharedCapacity
       * 具体见：WeakOrderQueue.allocate
       */
      	private AtomicInteger availableSharedCapacity;
      /**
       * DELAYED_RECYCLED中最多可存储的{Stack，WeakOrderQueue}键值对个数
       */
      	private int maxDelayedQueues;
      /**
       * 默认为8-1=7，即2^3-1，控制每8个元素只有一个可以被recycle，其余7个被扔掉
       */
      	private int ratioMask;
         
          /**
          * 创建好一个 Stack 对象之后，就会调用 stack.pop() 进行对象的获取
          */
          DefaultHandle<T> pop() {
                  // 赋值的原因：难道访问实例变量需要从内存中读取会导致效率降低？
              	// stack 可以看作线程的本地变量，不用考虑线程安全
                  int size = this.size;
                  // 1. size=0 则说明本线程的Stack没有可用的对象，先从其它线程中获取。
          		// 由于在transfer(Stack<?> dst)的过程中，可能将其他线程的WeakOrderQueue中的DefaultHandle对象
          		// 传递到当前的Stack，所以size发生了变化，需要重新赋值
                  if (size == 0) {
                      // 对象可能被其它线程回收了，回去把它们捞回来
                      if (!scavenge()) {
                          return null;
                      }
                      size = this.size;
                  }
                  // 2. 注意：因为一个Recycler<T>只能回收一种类型T的对象，所以element可以直接使用操作size来作为下标来进行获取
                  // 同时清空ret在Stack队列中的资源
                  size --;
                  DefaultHandle ret = elements[size];
                  elements[size] = null;
                  // 对象已经被取走了，回收id=0
                  ret.recycleId = 0;
                  ret.lastRecycledId = 0;
                  this.size = size;
              }
          
      }
      ```
      
      总结：
      
      **Recycler#get 总体步骤：**
      
      1. 如果 maxCapacityPerThread == 0，则禁用回收功能：新建 复用对象，将一个什么都不做的 NOOP_HANDLE 塞入 复用对象，进而不做回收操作；否则
      2. 获取当前线程的 Stack对象，如果没有，则新建一个 Stack 对象，在 Stack 对象的构造器中，会新建一个 DefaultHandle[]，默认大小为 4k；
      3. 从 stack 对象中 pop 出一个 DefaultHandle 来，如果不为 null，直接返回 DefaultHandle 存储的真实对象 value；如果为 null，则新建一个 DefaultHandle 对象，之后新建复用对象，并进行 DefaultHandle 对象和复用对象的绑定，最终返回复用对象
      
      **Stack#pop的步骤：**
      
      1. 首先获取当前的 Stack 中的 DefaultHandle 对象中的元素个数。
      2. 如果不为 0，直接获取 Stack 对象中 DefaultHandle[] 的最后一位元素，然后将该元素置为 null，之后做防护性检测，最后重置当前的 stack 对象的 size 属性以及获取到的 DefaultHandle 对象的 recycledId 和 lastRecycledId 回收标记，返回 DefaultHandle 对象。
      3. 如果为 0，则从其他线程的与当前的 Stack 对象关联的 WeakOrderQueue 中获取元素，并转移到 Stack 的 DefaultHandle[] 中（每一次 pop 只转移一个 Link），如果转移不成功，说明没有元素可用，直接返回 null；如果转移成功，则重置 size 属性为转移后的 Stack 的 DefaultHandle[] 的 size，之后按照第二步执行
      
      **同线程回收对象**
      
      ```java
      /**
      * Recycle#DefaultHandle
      */
      static final class DefaultHandle<T> implements Handle<T> {
          // 容器
          private Stack<?> stack;
          // 具体的池化对象
          private Object value;
      
          @Override
      	public void recycle(T object) {
              // 防护性判断
              if (object != value) {
                  throw new IllegalArgumentException("object does not belong to handle");
              }
      
              // https://github.com/netty/netty/issues/8220
              if (this.lastRecycledId != this.recycledId) {
                  throw new IllegalStateException("recycled already");
              }
              /**
               * 回收对象，this指的是当前的DefaultHandle对象
               */
              stack.push(this);
      	}
      }
      
      /***********************Recycle#Stack************************/
      
      /**
       * 每有一个元素将要被回收, 则该值+1，例如第一个被回收的元素的handleRecycleCount=handleRecycleCount+1=0
       * 与ratioMask配合，用来决定当前的元素是被回收还是被drop。
       * 例如 ++handleRecycleCount & ratioMask（7），其实相当于 ++handleRecycleCount % 8，
       * 则当 ++handleRecycleCount = 0/8/16/...时，元素被回收，其余的元素直接被drop
       */
      private int handleRecycleCount = -1;
      
      /**
      * 回收对象 Stack#push
      */
      void push(DefaultHandle<?> item) {
          Thread currentThread = Thread.currentThread();
          if (threadRef.get() == currentThread) {
              // 同线程回收
              // 1.判断对象是否被回收
              // 2.判断是否超过最大回收数量（会有一个回收频率，即不是每个对象都会回收）
              // 3.判断对象池容量是否足够，不够则扩容
              pushNow(item);
          } else {
              // 非当前线程创造的对象则异步回收
              pushLater(item, currentThread);
          }
      }
      
      /**
       * 两个drop的时机
       * 1、pushNow：当前线程将数据push到Stack中
       * 2、transfer：将其他线程的WeakOrderQueue中的数据转移到当前的Stack中
       */
      private boolean dropHandle(DefaultHandle<T> item) {
          if (!item.hasBeenRecycled) {
              // 每8个对象：扔掉7个，回收一个
              // 回收的索引：handleRecycleCount - 0/8/16/24/32/...
              if ((++handleRecycleCount & ratioMask) != 0) {
                  return true;
              }
              // 设置已经被回收了的标志，实际上此处还没有被回收，在pushNow(DefaultHandle<T> item)接下来的逻辑就会进行回收
              // 对于pushNow(DefaultHandle<T> item)：该值仅仅用于控制是否执行 (++handleRecycleCount & ratioMask) != 0 这段逻辑，而不会用于阻止重复回收的操作，重复回收的操作由item.recycleId | item.lastRecycledId来阻止
              item.hasBeenRecycled = true;
          }
          return false;
      }
      ```
      
      **DefaultHandle#recycle 步骤：**
      
      1. 首先 Recycler 对象做防护性检测，并且做多次回收的检测;之后向 stack 对象中 push 当前的 DefaultHandle 对象
      2. stack 先检测当前的线程是否是创建 stack 的线程，如果不是，则走异线程回收逻辑；如果是，则首先判断是否重复回收，然后判断 stack 的 DefaultHandle[] 中的元素个数是否已经超过最大容量（4k），如果是，直接返回；如果不是，则计算当前元素是否需要回收（netty 为了防止 Stack 的 DefaultHandle[] 数组发生爆炸性的增长，所以默认采取每 8 个元素回收一个，扔掉 7 个的策略），如果不需要回收，直接返回；如果需要回收，则
      3. 判断当前的 DefaultHandle[] 是否还有空位，如果没有，以 maxCapacity 为最大边界扩容 2 倍，之后拷贝旧数组的元素到新数组，然后将当前的 DefaultHandle 对象放置到 DefaultHandle[] 中
      4. 最后重置 stack.size 属
      
      **异步线程的回收**
      
      ```java
      /***********************Stack************************/
      /**
       * 该值是当线程B回收线程A创建的对象时，线程B会为线程A的Stack对象创建一个WeakOrderQueue对象，
       * 该WeakOrderQueue指向这里的head，用于后续线程A对对象的查找操作
       * Q: why volatile?
       * A: 假设线程A正要读取对象X，此时需要从其他线程的WeakOrderQueue中读取，假设此时线程B正好创建Queue，并向Queue中放入一个对象X；假设恰好次Queue就是线程A的Stack的head
       * 使用volatile可以立即读取到该queue。
       *
       * 对于head的设置，具有同步问题。具体见此处的volatile和synchronized void setHead(WeakOrderQueue queue)
       */
      private volatile WeakOrderQueue head;
      
      private void pushLater(DefaultHandle<?> item, Thread thread) {
                
           // 获取当前线程的‘仓库’
           Map<Stack<?>, WeakOrderQueue> delayedRecycled = DELAYED_RECYCLED.get();
           // 异线程回收的对象（Handle）就是存放在WeakOrderQueue的Link中的
           // 根据stack取到WeakOrderQueue
           WeakOrderQueue queue = delayedRecycled.get(this);
           // queue=null表示当前线程从未回收过其它线程的对象
           if (queue == null) {
               // 如果当前WeakOrderQueue数量大于最大值
               if (delayedRecycled.size() >= maxDelayedQueues) {
                   // 打上虚拟标记
                   delayedRecycled.put(this, WeakOrderQueue.DUMMY);
                   return;
               }
               // 创建WeakOrderQueue
               if ((queue = WeakOrderQueue.allocate(this, thread)) == null) {
                   // drop object
                   return;
               }
               delayedRecycled.put(this, queue);
           } else if (queue == WeakOrderQueue.DUMMY) {
               // drop object
               return;
           }
           // 将回收对象放回到队列中
           queue.add(item);
      }
      
      /**
       * 假设线程B和线程C同时回收线程A的对象时，有可能会同时newQueue，就可能同时setHead，所以这里需要加锁
       * 以head==null的时候为例，
       * 加锁：
       * 线程B先执行，则head = 线程B的queue；之后线程C执行，此时将当前的head也就是线程B的queue作为线程C的queue的next，组成链表，之后设置head为线程C的queue
       * 不加锁：
       * 线程B先执行queue.setNext(head);此时线程B的queue.next=null->线程C执行queue.setNext(head);线程C的queue.next=null
       * -> 线程B执行head = queue;设置head为线程B的queue -> 线程C执行head = queue;设置head为线程C的queue
       *
       * 注意：此时线程B和线程C的queue没有连起来，则之后的poll()就不会从B进行查询。（B就是资源泄露）
       */
      synchronized void setHead(WeakOrderQueue queue) {
          queue.setNext(head);
          head = queue;
      }
      
      /***********************Recycle#WeakOrderQueue************************/
      /**
       * 如果DELAYED_RECYCLED中的key-value对已经达到了maxDelayedQueues，
       * 对于后续的Stack，其对应的WeakOrderQueue设置为DUMMY，
       * 后续如果检测到DELAYED_RECYCLED中对应的Stack的value是WeakOrderQueue.DUMMY时，直接返回，不做存储操作
       */
      static final WeakOrderQueue DUMMY = new WeakOrderQueue();
      
      /**
       * WeakOrderQueue的唯一标记
       */
      private final int id = ID_GENERATOR.incrementAndGet();
      
      /**
       * 1、why WeakReference？与Stack相同。
       * 2、作用是在poll的时候，如果owner不存在了，则需要将该线程所包含的WeakOrderQueue的元素释放(也可能是转移到了 stack 中)，然后从链表中删除该Queue。
       */
      private final WeakReference<Thread> owner;
      
         // 对象仓库, 当 stack 中无法提供时，可以通过WeakOrderQueue并调用其transfer方法向stack供给对象
      private static <T> WeakOrderQueue newQueue(Stack<T> stack, Thread currentThread) {
          // 创建WeakOrderQueue
          WeakOrderQueue queue = new WeakOrderQueue(stack, currentThread);
          // 将该queue赋值给stack的head属性
          stack.setHead(queue);
      
          /**
           * 将新建的queue添加到Cleaner中，当queue不可达时，
           * 调用head中的run()方法回收内存availableSharedCapacity，否则该值将不会增加，影响后续的Link的创建
           */
          return queue;
      }
      
      static WeakOrderQueue allocate(Stack<?> stack, Thread thread) {
           
      // 判断对象所属stack还能不能分配LINK_CAPACITY内存，能就分配，不能就返回null
      //	1.LINK_CAPACITY 为啥是 16？或许是参考 内存 page 分配
      //  2.为啥会直接分配一个 Link 大小，毕竟只是回收一个对象，可以直接只使用一个Handler 
          // 一：一次性分配 16个 DefaultHandler，减少内存分配次数
          // 二：只需要一次校验 Link 就行了，不用每次都需要重新校验 Stack, WeakOrderQueue(只有当需要重新创建 Link 的时候才需要重新校验)
         return Head.reserveSpace(stack.availableSharedCapacity, LINK_CAPACITY)
                          ? newQueue(stack, thread) : null;
              }
          }
      
      /***********************WeakOrderQueue.Head************************/
      // Head仅仅作为head-Link的占位符，仅用于ObjectCleaner回收操作
      static final class Head {
          private final AtomicInteger availableSharedCapacity;
          /**
           * 指定读操作的Link节点，
           * eg. Head -> Link1 -> Link2
           * 假设此时的读操作在Link2上进行时，则此处的link == Link2，见transfer(Stack dst),
           * 实际上此时Link1已经被读完了，Link1变成了垃圾（一旦一个Link的读指针指向了最后，则该Link不会被重复利用，而是被GC掉，
           * 之后回收空间，新建Link再进行操作）
           */
          private Link link;
          
           /**
           * 在该对象被真正的回收前，执行该方法
           * 循环释放当前的WeakOrderQueue中的Link链表所占用的所有共享空间availableSharedCapacity，
           * 如果不释放，否则该值将不会增加，影响后续的Link的创建
           */
          @Override
          protected void finalize() throws Throwable {
              try {
                  super.finalize();
              } finally {
                  Link head = link;
                  // Unlink to help GC
                  link = null;
                  while (head != null) {
                      reclaimSpace(LINK_CAPACITY);
                      Link next = head.next;
                      // Unlink to help GC and guard against GC nepotism.
                      head.next = null;
                      head = next;
                  }
              }
          }
      
      }
      ```
      
      **异步线程获取对象**
      
      ```java
      /***********************Recycle#Stack************************/
      /**
       * cursor：当前操作的WeakOrderQueue
       * prev：cursor的前一个WeakOrderQueue
       */
      private WeakOrderQueue cursor, prev;
      /***********************Recycle#Stack#pop************************/
      /**
       * 从‘仓库’中回收对象
       */
      boolean scavenge() {
      	// ...
      }
      
      boolean scavengeSome() {   
           // cursor指向当前回收的目标WeakOrderQueue
           WeakOrderQueue cursor = this.cursor;
           // cursor == null表示其它线程没有回收当前stack产出的对象
           if (cursor == null) {          
           }
           // 根据游标遍历其它WeakOrderQueue循环回收
           do {
               // 尝试捞回当前stack(this)产出的对象，每次捞回一个Link大小
               if (cursor.transfer(this)) {
                   success = true;
                   break;
               }
               WeakOrderQueue next = cursor.next;
               // 如果当前 WeakOrderQueue 的线程为null，则说明线程已经不存在了，但WeakOrderQueue对象还在
               // 1.回收数据
               // 2.移除链表
               if (cursor.owner.get() == null) {
                   // 如果此 WeakOrderQueue 还有数据，则尝试捞回这些对象
                   if (cursor.hasFinalData()) {
                       // 将所有数据捞回
                   }
                   if (prev != null) {
                       // cursor、prev、next都是指向WeakOrderQueue
                       // 下面代码就是释放cursor
                       /*
                        * prev(thread-4) -> cursor(killed) -> next(thread-2)-> 
                        *
                        * prev(thread-4) -> next(thread-2)-> ...
                        */
                       prev.setNext(next);
                   }
               } 
           } while (cursor != null && !success);
      }
      ```
      
    - 总结
    
      - 线程同步问题
        1. 假设线程 A 进行 get，线程 B 也进行 get，无锁（二者各自从自己的 stack 或者从各自的 weakOrderQueue 中进行获取）
        2. 假设线程 A 进行 get 对象 X，线程 B 进行 recycle 对象 X，无锁（假设线程 A 无法直接从其 Stack 获取，从 WeakOrderQueue 进行获取，由于 stack.head 是 volatile 的，线程 B recycle 的对象 X 可以被线程 A 立即获取）
        3. 假设线程 C 和线程 B recycle 线程 A 的对象 X，此时需要加锁：假设线程 B 和线程 C 同时回收线程 A 的对象时，有可能会同时 newQueue，就可能同时 setHead，所以这里需要加锁
        4. 仍然是以空间换时间的概念，在实际使用中，Recycler 几乎是一个无锁的对象回收池
      - 防止资源泄漏
        1. 当我们删除 FastThreadLocal<Stack> threadLocal 中线程 A 的 stack 对象时，会从 FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED 对象（注意：该对象是类变量）中删除 key 为 stack 对象的 Entry；
        2. FastThreadLocal<Map<Stack, WeakOrderQueue>> DELAYED_RECYCLED 使用 WeakHashMap：当 Stack 没有强引用可达时，整个 Entry{Stack, WeakOrderQueue} 都会加入相应的弱引用队列等待回收；
        3. Stack.WeakReference threadRef：假设该线程对象在外界已经没有强引用了，那么实际上该线程对象就可以被回收了。但是如果 Stack 用的是强引用，那么虽然外界不再对该线程有强引用，但是该 stack 对象还持有强引用（假设用户存储了 DefaultHandle 对象，然后一直不释放，而 DefaultHandle 对象又持有 stack 引用），导致该线程对象无法释放（for example ?）
        4. WeakOrderQueue.WeakReference owner：与 Stack.WeakReference threadRef 相同。
           将 DefaultHandle item 添加到 WeakOrderQueue中时，需要 item.stack = null：如果使用者在将 DefaultHandle 对象压入队列后，将 Stack 设置为 null，但是此处的 DefaultHandle 是持有 stack 的强引用的，则 Stack 对象无法回收；而且由于此处 DefaultHandle 是持有 stack 的强引用，WeakHashMap 中对应 stack 的 WeakOrderQueue 也无法被回收掉了，导致内存泄漏。
        5. Head.finalize()：在该对象被真正的回收前，会循环释放当前的 WeakOrderQueue 中的 Link 链表所占用的所有共享空间 availableSharedCapacity，如果不释放，否则该值将不会增加，影响后续的Link的创建
      
    - 流程图

​					 Thread2 回收  Thread1 的对象

![](C:\Users\guangyong.deng\Desktop\博客\Netty\recycle-1.png)

​					Thread3 回收 Thread1 的对象

![](C:\Users\guangyong.deng\Desktop\博客\Netty\recycle-2.png)

- Jctools

  - MPSC

    - 数据结构

      1. 环形数组
      2. 通过一个生产者索引，消费者索引控制
      3. 通过一个 mask 计算下标

      ```java
      // 消费者的索引，可能大于数组的长度
      private volatile long consumerIndex;
      
      // 生产者的下标的最大值，动态变化，用来判断队列是否已满
      private volatile long producerLimit;
      
      // 生产者的索引，可能大于数组的长度
      private volatile long producerIndex;
      
      // 为了&计算下标，初始化值为：容量-1
      protected final long mask;
          
      // 存放数据的数组
      protected final E[] buffer;
      ```

    - offer

      ```java
      /******************** MpscArrayQueue *************************/
      public boolean offer(final E e) {
          
          // 1.获取索引
          // 2.获取生产者最大索引
           final long mask = this.mask;
          long producerLimit = lvProducerLimit();
          long pIndex;
          
           do {
                  // 获取生产者索引
                  pIndex = lvProducerIndex();
                  // 队列已满
                  if (pIndex >= producerLimit) {
                      // 消费者索引
                      final long cIndex = lvConsumerIndex();
                      // 说明生产者已经走到数组的尾部，需要从头开始生产
                      // 更新producerLimit
                      /**
      				* producerLimit = 8 = 1000,  mask = 0111 = 7, pIndex = 8
      				* 1.假设 cIndex = 0, 即没有消费者， producerLimit = 8, 返回 false
      				* 2.假设 cIndex = 1, producerLimit = 9, 重新设置 producerLimit
      				* 环形数组思想 --- 已经消费的元素会被直接覆盖
      				*/
                      producerLimit = cIndex + mask + 1;
      
                      if (pIndex >= producerLimit){
                          return false; // FULL :(
                      }
                      else {
                          
                          soProducerLimit(producerLimit);
                      }
                  }
              }while (!casProducerIndex(pIndex, pIndex + 1));
           // 计算该索引对应数组元素的下标地址
           final long offset = calcCircularRefElementOffset(pIndex, mask);
           // 将对应下标地址元素修改为 e
           soRefElement(buffer, offset, e);
           return true; // AWESOME :)
          
      }
      
      
       public E poll()
          {
              final long cIndex = lpConsumerIndex();
              final long offset = calcCircularRefElementOffset(cIndex, mask);
              // Copy field to avoid re-reading after volatile load
              final E[] buffer = this.buffer;
              // 由于
              E e = lvRefElement(buffer, offset);
           	// 由于获取元素可能有延迟，所以还需有处理
              if (null == e)
              {
                  
                  if (cIndex != lvProducerIndex())
                  {
                      do
                      {
                          e = lvRefElement(buffer, offset);
                      }
                      while (e == null);
                  }
                  else
                  {
                      return null;
                  }
              }
      
              spRefElement(buffer, offset, null);
              soConsumerIndex(cIndex + 1);
              return e;
          }
      
      
       public static long calcCircularRefElementOffset(long index, long mask)
       {
           // 左移2位相当于乘4, 1 -> 4，起始地址为 16，每个元素大小为 4，所以第一个元素的内存地址为 20
           return REF_ARRAY_BASE + ((index & mask) << REF_ELEMENT_SHIFT);
       }
      
      
      public static <E> void soRefElement(E[] buffer, long offset, E e)
      {
          UNSAFE.putOrderedObject(buffer, offset, e);
      }
      
      /************************* UnsafeRefArrayAccess ********************/
      // 数组buffer的相对偏移地址
      public static final long REF_ARRAY_BASE;
      
      // 数组单个元素的偏移量
      public static final int REF_ELEMENT_SHIFT;
      
      static
          {
          	// 64-位 windwos 上是返回 4个字节（Object 对象引用的大小为 4个字节大小？）
              final int scale = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);
              if (4 == scale)
              {
                  REF_ELEMENT_SHIFT = 2;
              }
              else if (8 == scale)
              {
                  REF_ELEMENT_SHIFT = 3;
              }
              else
              {
                  throw new IllegalStateException("Unknown pointer size: " + scale);
              }
          	
              REF_ARRAY_BASE = UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class);
          }
      
      /************************* Unsafe *********************************/
      
      /************************ 数组操作 *********************************/
      // 获取数组中单个元素占用的字节数
      public native int arrayIndexScale(Class<?> arrayClass);
      
      // 数组元素起始地址输出都一致，猜测：所有数组元素都放在一块内存中存放
      public native int arrayBaseOffset(Class<?> arrayClass);
      
      
      /************************ 对象操作 ********************************/
      // 前置知识：内存屏障，多线程
      // 有序、延迟版本的 putObjectVolatile 方法，不保证值的改变被其他线程立即看到。只有在field被volatile修饰符修饰时有效
      // 关于 putOrderXXX 与 putXXXVolatile 的不同之处：
      // 1.内存屏障的不同 前者使用 store-store屏障，后者使用 store-load屏障，具有一定的性能差距（待考证）
      public native void putOrderedObject(Object o, long offset, Object x);
      
      // 对指定的地址进行赋值，保证值的改变被其他线程立即看到，使用volatile的存储语义
      public native void putObjectVolatile(Object o, long offset, Object x);
      ```
      

-  netty 中使用的算法

  - 自适应分配缓存区算法

  - 分配内存算法（JEMalloc）

    - 实现原理

      根据请求内存的大小而采取不同的分配策略，如电商送货，小件商品直接从同城仓库送出；当顾客采购大件商品（比如电视）时，从区域仓库送出；当顾客采购超大件商品（比如汽车）时，则从全国仓库送出

      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\jemalloc.webp)
  
      [论文中文翻译]: https://blog.csdn.net/stillingpb/article/details/50937366
      [原文]: http://people.freebsd.org/~jasone/jemalloc/bsdcan2006/jemalloc.pdf
  
      
  
    - 代码
  
      - PoolSubpage
      
        前置知识：PoolSubpage双向链表分配内存小于PageSize（Netty默认8KB）的请求
        会将 Page 切分成更小的节点，（具体如何切分？），然后 subpage 的最小切分为 16B
      
      ```java
      /****************** PoolSubpage *****************/
      // 所属的Chunk
      final PoolChunk<T> chunk;
      
      // 所属Page的标号
      private final int memoryMapIdx;
      
      // 页大小
      private final int pageSize;
      
      /**
      * 位图中的元素表示在PoolSubpage中分配的结果，每一个 bit 代表一个节点是否分配，由于使用 数组代表位图，显然需要有两个个坐标才能确认一个节点是否分配
      1. 数组索引 i  2. bitmap[i] 中的偏移量 offset  3.netty 使用一个 int 记录前两个坐标
         |<--   26   -->| <--   6      -->  |
         |  long数组偏移  |  long的二进制位偏移 |
         
         举个例吧：同样 pageSize = 8192 ，申请一个 512，分为 64 个内存段，需要 64 个 bit 信息表示分配，所以只需要 一个 long
         即 bitmap[0] 可以代表所有的内存段
         [0,0, 0, 0, 0, ................. 0] 
         
         假设 分配了两个内存段 bitmap[0] -> [0, 0, 0, .........0, 1, 1]
         那么下一个内存段 i -> 0, offset -> 000100
      *
      */
      private final long[] bitmap;
      
      // arena双向链表的后继节点
      PoolSubpage<T> next;
          
      // arena双向链表的前驱节点
      PoolSubpage<T> prev;
      // 该page切分后每一个节点的大小
      int elemSize;
      // 该page包含的最大节点数量
      private int maxNumElems; 
      // 下一个可用的节点位置
      private int nextAvail;
      // 可用的节点数量
      private int numAvail;     
      
      
      PoolSubpage(...) {
          	// 赋值操作
          	...
          	// 此处使用最大值，最小分配16B所需的long个数
          	// pageSize / 16 / 64 == pageSize >>> 10
          	// TODO: 假设一个 pageSize 为 8k = 8192（默认）
          	// 一：/16 代表什么？ 表示分配的 elemSize 最小为 16B，那么可以切分为 8192 / 16 = 512 个节点
          	// 二：/64 代表什么？ 表示使用一个 bit代表使用信息，则代表需要 512 / 64 = 8 个 long 就可以表示完成所有的位图信息
          	// 三：为啥不使用 pageSize / eleSize / 64，是因为考虑到复用。当一个PoolSubpage以32B均等切分，然后释放返回给Chunk，当Chunk再次被分配时，比如16B，此时只需调用init()方法即可而不再需要初始其他数据（即按最大长度使用即可）
              bitmap = new long[pageSize >>> 10]; 
              init(head, elemSize);
          }
      
      void init(PoolSubpage<T> head, int elemSize) {
          doNotDestroy = true;
          this.elemSize = elemSize;
          if (elemSize != 0) {
              maxNumElems = numAvail = pageSize / elemSize;
              nextAvail = 0;
              // 表示需要多少个 long 才能表示使用情况
              // 注意：这是实际使用的个数，而 bitmap.length 是使用的最大个数
              bitmapLength = maxNumElems >>> 6;
              // subpage不是64倍，多需要一个long
              // 等效来说 == maxNumElems % 64 != 0
              if ((maxNumElems & 63) != 0) {
                  bitmapLength ++;
              }
              // 初始化位图信息
              for (int i = 0; i < bitmapLength; i ++) {
                  bitmap[i] = 0;
              }
          }
          // 将该 PoolSubpage 加入到 Arena 的双向链表中
          addToPool(head);
      }
      
      long allocate() {
          if (elemSize == 0) {
              return toHandle(0);
          }
      	// 1.没有空闲节点
          // 2.需要销毁（TODO: 什么时候会发送销毁？）
          if (numAvail == 0 || !doNotDestroy) {
              return -1;
          }
      	// 找到当前page中可分配内存段的 bitmapIdx
          final int bitmapIdx = getNextAvail();
          // 寻找数组索引，32 - 6 = 26 
          int q = bitmapIdx >>> 6;
          int r = bitmapIdx & 63;
          assert (bitmap[q] >>> r & 1) == 0;
          bitmap[q] |= 1L << r;
      
          if (-- numAvail == 0) {
              removeFromPool();
          }
      
          return toHandle(bitmapIdx);
      }
      
      private int getNextAvail() {
          // 指向了下一个可分配的内存段
          int nextAvail = this.nextAvail;
          // 如果大于 0，说明指向了一个有效的内存段，可以直接返回
          // 每次分配完成后，都会重置为 -1，所以下一次分配，需要重新调用 findNextAvail
          if (nextAvail >= 0) {
              this.nextAvail = -1;
              return nextAvail;
          }
          return findNextAvail();
      }
      
      private int findNextAvail() {
          final long[] bitmap = this.bitmap;
          final int bitmapLength = this.bitmapLength;
          // 遍历位图数组，找到还有未分配的内存段
          for (int i = 0; i < bitmapLength; i ++) {
              long bits = bitmap[i];
              // 表示该段还有未分配的内存
              if (~bits != 0) {
                  return findNextAvail0(i, bits);
              }
          }
          return -1;
      }
      
      private int findNextAvail0(int i, long bits) {
          final int maxNumElems = this.maxNumElems;
          final int baseVal = i << 6;
      	 // long从低位开始表示分配信息，最低位表示第1块分配
          for (int j = 0; j < 64; j ++) {
              // 判断这个位置是否被分配
              if ((bits & 1) == 0) {
                  // 获取偏移量
                  int val = baseVal | j;
                  if (val < maxNumElems) {
                      return val;
                  } else {
                      break;
                  }
              }
              bits >>>= 1;
          }
          return -1;
      }
      
      boolean free(PoolSubpage<T> head, int bitmapIdx) {
              // 清除使用信息
              bitmap[q] ^= 1L << r;
      		// 设置为可用于下次分配的位置（分配的时候做判断的原因！）
              setNextAvail(bitmapIdx);
      
             // 一些状态判断
            // 返回 true 说明 pageSub 说明正在使用
            // 返回 false 说明 没有使用，可以回收
      }
      
      // 分配完成后的信息，使用一个 long 来表示
      |<--   24   -->| <--   6      --> | <--         32         --> |
      |  long数组偏移 |  long的二进制位偏移|       所属Chunk标号         |
      private long toHandle(int bitmapIdx) {
          return 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
      }
      ```
      
      - PoolChunkList
      
        Chunk块随着内存使用率的变化，有六种状态：QINIT，Q0，Q25，Q50，Q75，Q100，可知，一种状态可能有多个Chunk块，Netty使用PoolChunkList来存储这些Chunk块，它们之间的关系如下图所示：
      
        ![](C:\Users\guangyong.deng\Desktop\博客\Netty\chunklist.webp)
      
        随着 Chunk 块内存使用率的不同，会在不同状态之间转化，各个状态如下：
      
        | 状态  |  最小内存使用率   | 最大内存使用率 |
        | :---: | :---------------: | :------------: |
        | QINIT | Integer.MIN_VALUE |       25       |
        |  Q0   |         1         |       50       |
        |  Q25  |        25         |       75       |
        |  Q50  |        50         |      100       |
        |  Q75  |        75         |      100       |
        | Q100  |        100        |      100       |
      
      ```java
      /**************** PoolChunkList **************/
      // 所属的Arena
      private final PoolArena<T> arena;
      // 下一个状态
      private final PoolChunkList<T> nextList;
      // 上一个状态
      private PoolChunkList<T> prevList; 
      // 状态的最小内存使用率
      private final int minUsage; 
      // 状态的最大内存使用率
      private final int maxUsage; 
      // 该状态下的一个Chunk可分配的最大字节数
      private final int maxCapacity; 
      // head节点，插入链表是使用头插法（相当于尾节点）
      private PoolChunk<T> head; 
      // 可分配空间的最小字节数阈值
      private final int freeMinThreshold;
      // 同理，最大字节数阈值
      private final int freeMaxThreshold;
      
      PoolChunkList(...) {
          // 计算最大字节数
          maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
          
          // 源注释说的比较清楚了，简单的数学转换，注意一下除法带来的精度缺失就行了
          freeMinThreshold = ...
          freeMaxThreshold = ...
      }
      
      private static int calculateMaxCapacity(int minUsage, int chunkSize) {
          minUsage = minUsage0(minUsage);
      
          if (minUsage == 100) {
              // 说明已经没有多余空闲内存可以分配了
              // Q100 不能再分配
              return 0;
          }
      	// Q25中一个Chunk可以分配的最大内存为0.75 * ChunkSize
          return  (int) (chunkSize * (100L - minUsage) / 100L);
      }
      
      // 说明正在分配内存，可以看作状态向右改变
      void add(PoolChunk<T> chunk) {
          if (chunk.freeBytes <= freeMinThreshold) {
              // 递归调用，直到找到合适的状态加入
              nextList.add(chunk);
              return;
          }
          // 链表操作
          add0(chunk);
      }
      
      // 可以看作状态向左改变
      private boolean move(PoolChunk<T> chunk) {
          assert chunk.usage() < maxUsage;
      	// 说明是在释放内存
          if (chunk.freeBytes > freeMaxThreshold) {
              // 进入上一个状态.，同样递归操作
              return move0(chunk);
          }
      
          // 加入链表
          add0(chunk);
          return true;
      }
      
      private boolean move0(PoolChunk<T> chunk) {
          if (prevList == null) {
              // 此时表示chunk为Q0状态，且还需要移动，说明Chunk使用率为0，占有的内存可以被释放
              assert chunk.usage() == 0;
              return false;
          }
          return prevList.move(chunk);
      }
      
      boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity, PoolThreadCache threadCache) {
           // 申请的内存已超过一个Chunk块可以分配的最大内存
          if (normCapacity > maxCapacity) {
              return false;
          }
      	// 遍历 PoolChunkList 查看是否能够分配成功
          for (PoolChunk<T> cur = head; cur != null; cur = cur.next) {
              // 找到一个 PoolChunk 的空闲内存能够分配
              if (cur.allocate(buf, reqCapacity, normCapacity, threadCache)) {
                  // PoolChunk 分配之后状态可能发生变化
                  if (cur.freeBytes <= freeMinThreshold) {
                      remove(cur);
                      nextList.add(cur);
                  }
                  return true;
              }
          }
          return false;
      }
      
      // 没什么好说的，主要是注意状态的改变
      boolean free(PoolChunk<T> chunk, long handle, ByteBuffer nioBuffer) {
          chunk.free(handle, nioBuffer);
          if (chunk.freeBytes > freeMaxThreshold) {
              remove(chunk);
              // Move the PoolChunk down the PoolChunkList linked-list.
              return move0(chunk);
          }
          return true;
      }
      
      /*************** PoolArena ********************/
      q100 = new PoolChunkList<T>(this, null, 100, Integer.MAX_VALUE, chunkSize);
      q075 = new PoolChunkList<T>(this, q100, 75, 100, chunkSize);
      q050 = new PoolChunkList<T>(this, q075, 50, 100, chunkSize);
      q025 = new PoolChunkList<T>(this, q050, 25, 75, chunkSize);
      q000 = new PoolChunkList<T>(this, q025, 1, 50, chunkSize);
      qInit = new PoolChunkList<T>(this, q000, Integer.MIN_VALUE, 25, chunkSize);
      
      q100.prevList(q075);
      q075.prevList(q050);
      q050.prevList(q025);
      q025.prevList(q000);
      q000.prevList(null);
      qInit.prevList(qInit);
      ```
      
      - PoolChunk
      
        JEMalloc 分配算法使用伙伴分配算法分配 Chunk 中的 Page 节点。Netty使用两个字节数组`memoryMap`和`depthMap`来表示两棵二叉树，其中`MemoryMap`存放分配信息，`depthMap`存放节点的高度信息。（那么问题来了：如果确定一个节点是否被分配，或者是否能够分配请求的内存？）
      
        ![](C:\Users\guangyong.deng\Desktop\博客\Netty\chunk.webp)
      
        左图表示每个节点的编号，注意从1开始（更方便计算子节点，因为是满二叉树），数组索引即代表节点编号（memoryMap）
      
        右图表示每个节点的深度，注意从0开始（深度和高度的定义有所不同）（depthMap）
      
        初始状态时，`memoryMap`和`depthMap`相等
      
        （例如：memoryMap[512] = depthMap[512] = 9）
      
        `depthMap`的值初始化后不再改变，`memoryMap` 的值则随着节点分配而改变。当一个节点被分配以后，该节点的值设置为12（最大高度+1）表示不可用，并且会更新祖先节点的值。下图表示随着4号节点分配而更新祖先节点的过程，其中每个节点的第一个数字表示节点编号，第二个数字表示节点高度值。
      
        ![](C:\Users\guangyong.deng\Desktop\博客\Netty\chunk-process.webp)
      
        过程：
      
        1. 4号节点被完全分配，将高度值设置为12表示不可用。
        2. 4号节点的父亲节点即2号节点，将高度值更新为两个子节点的较小值；其他祖先节点亦然，直到高度值更新至根节点。
      
        如何快速确定能不能分配：
      
        1. memoryMap[id] = depthMap[id] -- 该节点没有被分配
      
        2. memoryMap[id] > depthMap[id] -- 至少有一个子节点被分配，不能再分配该高度满足的内存，但可以根据实际分配较小一些的内存。比如，上图中分配了4号子节点的2号节点，值从1更新为2，表示该节点不能再分配8MB的只能最大分配4MB内存，因为分配了4号节点后只剩下5号节点可用。
      
        3. mempryMap[id] = 最大高度 + 1（本例中12） -- 该节点及其子节点已被完全分配， 没有剩余空间。
      
        
      
      ```java
      /****************** PoolChunk ****************/
      
      // 所属的 arena
      final PoolArena<T> arena;
      // 实际的内存块
      final T memory; 
      // 是否非池化
      final boolean unpooled; 
      // 分配信息二叉树
      private final byte[] memoryMap;
      //  高度信息二叉树
      private final byte[] depthMap;
      // subpage节点数组
      private final PoolSubpage<T>[] subpages;
      // 页大小，默认8KB=8192
      private final int pageSize; 
      // 最大高度，默认11
      private final int maxOrder; 
      // chunk块大小，默认16MB
      private final int chunkSize;
      // 标记节点不可用，最大高度 + 1， 默认12
      private final byte unusable; 
      // 可分配字节数
      private int freeBytes; 
      
      // 还有一些暂时未理解的属性
      
      private final int subpageOverflowMask;
      
      // 从1开始左移到页大小的位置，默认13，1<<13 = 8192
      private final int pageShifts;
      
      // log2(16MB) = 24
      private final int log2ChunkSize;
      
      // 切分成 subpage 节点的个数
      private final int maxSubpageAllocs;
      
      // 池化
      PoolChunk(...) {
          
          // TODO: 为啥最大的 subpage 数量会设置为这个值
          // 猜测：有多少个底层叶子节点就应该有多少个 subpage
          // 即 subpages = 叶子节点个数 = 1 << maxOrder
          maxSubpageAllocs = 1 << maxOrder;
          
          // new PoolSubpage[size]
          subpages = newSubpageArray(maxSubpageAllocs);
          
          
           memoryMap = new byte[maxSubpageAllocs << 1];
           depthMap = new byte[memoryMap.length];
          // 初始化，和上面的理论也对的上
          int memoryMapIndex = 1;
              for (int d = 0; d <= maxOrder; ++ d) { 
                  int depth = 1 << d;
                  for (int p = 0; p < depth; ++ p) {
                     
                      memoryMap[memoryMapIndex] = (byte) d;
                      depthMap[memoryMapIndex] = (byte) d;
                      memoryMapIndex ++;
                  }
              }
      }
      
      // 至少分配一个 page 
      private long allocateRun(int normCapacity) {
           // 计算满足需求的节点的高度
           // 假设请求为 8 KB 8KB = 8 * 1024 = 2^3 * 2^10 = 13
          int d = maxOrder - (log2(normCapacity) - pageShifts);
          // 获取未分配节点id
          int id = allocateNode(d);
          if (id < 0) {
              return id;
          }
          freeBytes -= runLength(id);
          return id;
      }
      
      private long allocateSubpage(int normCapacity) {
          // 找到arena中对应的subpage头节点   
          PoolSubpage<T> head = arena.findSubpagePoolHead(normCapacity);
          // subpage只能在二叉树的最大高度分配即分配叶子节点
          int d = maxOrder;
          synchronized (head) {
              int id = allocateNode(d);
              if (id < 0) {
                  return id;
              }
      
              final PoolSubpage<T>[] subpages = this.subpages;
              final int pageSize = this.pageSize;
      
              freeBytes -= pageSize;
      		
              // 得到叶子节点的偏移索引，从0开始，即2048-0,2049-1,...
              int subpageIdx = subpageIdx(id);
              PoolSubpage<T> subpage = subpages[subpageIdx];
              if (subpage == null) {
                  subpage = new PoolSubpage<T>(head, this, id, runOffset(id), pageSize, normCapacity);
                  subpages[subpageIdx] = subpage;
              } else {
                  subpage.init(head, normCapacity);
              }
              return subpage.allocate();
          }
      }
      
      ```
      
      - PoolThreadCache
      
        对于Tiny/Small、Normal大小的请求，优先从线程缓存中分配，通过内部的一个数据结果 MemoryRegionCache 进行维护，其可以看作内部是一个`ByteBuf`队列，其中的 ByteBuf 都是不再使用的，根据分配请求大小的不同，`MemoryRegionCache`可以分为Tiny，Small，Normal三种。
      
        其中`ByteBuf`队列的长度是有限制的，Tiny、Small、Normal依次为512、256、64。在线程缓存中，待回收的空间根据大小排列，比如，最大空间为16B的`ByteBuf`被缓存时，将被置于数组索引为1的`MemoryRegionCache`中，其中又由其中的队列存放该`ByteBuf`的空间信息，队列的最大长度为512。也就是说，16B的`ByteBuf`空间可以缓存512个，512B可以缓存256个，8KB可以缓存64个。
      
        如下：
      
        
      
      ```java
      /***************** MemoryRegionCache ****************/
      // 队列长度
      private final int size;
      // 队列
      private final Queue<Entry<T>> queue;
      // Tiny/Small/Normal
      private final SizeClass sizeClass;
      // 分配次数
      private int allocations;
      
      MemoryRegionCache(int size, SizeClass sizeClass) {
          this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
          // TODO: 为什么使用 Mpsc 
          // ByteBuf的分配和释放可能在不同的线程中，这里的多生产者即多个不同的释放线程，这样才能保证多个释放线程同时释放ByteBuf时所占空间正确添加到队列中
          queue = PlatformDependent.newFixedMpscQueue(this.size);
          this.sizeClass = sizeClass;
      }
      
      // 尝试缓存，从这里也可以看出，其实缓存的还是 ByteBuffer
      public final boolean add(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle) {
          Entry<T> entry = newEntry(chunk, nioBuffer, handle);
          boolean queued = queue.offer(entry);
          // 缓存满了
          if (!queued) {
              entry.recycle();
          }
          return queued;
      }
      
      public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
          Entry<T> entry = queue.poll();
          if (entry == null) {
              return false;
          }
           // 在之前ByteBuf同样的内存位置分配一个新的`ByteBuf`对象
           // 做一些参数校验：包括判断内存是否已经被回收等等
          initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity, threadCache);
          // entry对象回收利用
          entry.recycle();
      
          // 不用考虑线程安全，内存分配只会在同一个线程中执行，但是释放不一定
          ++ allocations;
          return true;
      }
      
      // 防止内存泄漏
      public final void trim() {
          // allocations 表示已经重新分配出去的ByteBuf个数
          int free = size - allocations;  
          allocations = 0;
      
          // 在一定阈值内还没被分配出去的空间将被释放
          if (free > 0) {
              free(free); // 释放队列中的节点
          }
      }
      
      private int free(int max, boolean finalizer) {
          int numFreed = 0;
          for (; numFreed < max; numFreed++) {
              Entry<T> entry = queue.poll();
              if (entry != null) {
                  freeEntry(entry, finalizer);
              } else {
                  // all cleared
                  return numFreed;
              }
          }
          return numFreed;
      }
      
      /************** Entry ****************/
      // 对象池
      final Handle<Entry<?>> recyclerHandle;
      // 所属 chunk
      PoolChunk<T> chunk;
      ByteBuffer nioBuffer;
      // ByteBuf在Chunk中的分配信息
      long handle = -1;
      
      
      /**************** PoolThreadCache *********/
      
      // 各类型的Cache数组
      private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
      
      ...
      
      // 分配次数
      private int allocations;    
      // 分配次数到达该阈值则检测释放
      private final int freeSweepAllocationThreshold; 
      // 线程结束观察者
      private final Thread deathWatchThread; 
      
      boolean add(PoolArena<?> area, PoolChunk chunk, ByteBuffer nioBuffer,
                  long handle, int normCapacity, SizeClass sizeClass) {
          // 在缓存数组中找到符合的元素
          MemoryRegionCache<?> cache = cache(area, normCapacity, sizeClass);
          if (cache == null) {
              return false;
          }
          return cache.add(chunk, nioBuffer, handle);
      }
      
      private MemoryRegionCache<?> cache(PoolArena<?> area, int normCapacity, SizeClass sizeClass) {
          switch (sizeClass) {
              case Normal:
                  return cacheForNormal(area, normCapacity);
              case Small:
                  return cacheForSmall(area, normCapacity);
              case Tiny:
                  return cacheForTiny(area, normCapacity);
              default:
                  throw new Error();
          }
      }
      
      private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
           // normCapacity >>> 4, 即16B的索引为1
          // 即 32B 的索引为2
          // 496B 的索引为 8
          int idx = PoolArena.tinyIdx(normCapacity);
          if (area.isDirect()) {
              return cache(tinySubPageDirectCaches, idx);
          }
          return cache(tinySubPageHeapCaches, idx);
      }
      
      // allocate tiny cache
      boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
          return allocate(cacheForTiny(area, normCapacity), buf, reqCapacity);
      }
      
      private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity) {
          if (cache == null) {
              return false;
          }
          boolean allocated = cache.allocate(buf, reqCapacity, this);
           // 分配次数达到整理阈值（包括所有的 cache）
          if (++ allocations >= freeSweepAllocationThreshold) {
              allocations = 0;
              // 整理
              trim();
          }
          return allocated;
      }
      
      void free(boolean finalizer) {
          // 遍历所有 cache 数组，进行释放
          free(tinySubPageHeapCaches, finalizer) + ...
      }
      
      private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
         
          for (MemoryRegionCache<?> c: caches) {
              // cache.free(finalizer)
              numFreed += free(c, finalizer);
          }
          return numFreed;
      }
      
      /************* PoolThreadLocalCache ***********/
      // 是否使用缓存
      private final boolean useCacheForAllThreads;
      
      @Override
      protected synchronized PoolThreadCache initialValue() {
          // 使得线程均等使用Arena
          final PoolArena<byte[]> heapArena = leastUsedArena(heapArenas);
          final PoolArena<ByteBuffer> directArena = leastUsedArena(directArenas);
      
          final Thread current = Thread.currentThread();
          // 启用缓存或者是FastThreadLocalThread即Netty中的IO线程
          if (useCacheForAllThreads || current instanceof FastThreadLocalThread) {
              final PoolThreadCache cache = ...
              return cache;
          }
          // No caching so just use 0 as sizes.
          return new PoolThreadCache(heapArena, directArena, 0, 0, 0, 0, 0);
      }
      
      private <T> PoolArena<T> leastUsedArena(PoolArena<T>[] arenas) {
      	// 寻找使用缓存最少的arena，保证每个Arena都能均等分到线程数，从而平均Arena的负载
          // TODO：关于arena 与线程的关系需要捋清楚
          PoolArena<T> minArena = arenas[0];
          for (int i = 1; i < arenas.length; i++) {
              PoolArena<T> arena = arenas[i];
              if (arena.numThreadCaches.get() < minArena.numThreadCaches.get()) {
                  minArena = arena;
              }
          }
      
          return minArena;
      }
      ```
      
      - PoolArena
      
        `PoolArena`分为两类：Heap和Direct
      
      ```java
      /****************** PoolArenaMetric ************/
      // 该接口的作用是做一些信息统计
      
      // 返回该线程绑定的 arena 个数
      int numThreadCaches();
      // 其他方法类似
      ...
      
      /****************** PoolArena ******************/
      // 分配内存的大小
      enum SizeClass {
          Tiny,
          Small,
          Normal 
      }
      
      private final int maxOrder;
      // 单个page的大小
      final int pageSize;
      // 用于辅助计算
      final int pageShifts; 
      // chunk的大小
      final int chunkSize; 
      // 用于判断请求是否为Small/Tiny
      final int subpageOverflowMask;
      // small请求的双向链表个数
      final int numSmallSubpagePools;
       // 对齐基准
      final int directMemoryCacheAlignment;
      // 用于对齐内存
      final int directMemoryCacheAlignmentMask;
      // Subpage双向链表（将 subpage 中按同一大小切分的划分为一组）
      private final PoolSubpage<T>[] tinySubpagePools;
      private final PoolSubpage<T>[] smallSubpagePools;
      
      // chunk 状态
      
      // 构造方法
      ...
      
      // 进行 subpage 分配的分组
      PoolSubpage<T> findSubpagePoolHead(int elemSize) {
          int tableIdx;
          PoolSubpage<T>[] table;
          if (isTiny(elemSize)) { // < 512 Tiny
              tableIdx = elemSize >>> 4;
              table = tinySubpagePools;
          } else {    // Small
              tableIdx = 0;
              elemSize >>>= 10;   // 512=0, 1KB=1, 2KB=2, 4KB=3
              while (elemSize != 0) {
                  elemSize >>>= 1;
                  tableIdx ++;
              }
              table = smallSubpagePools;
          }
      
          return table[tableIdx];
      }
      
      PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
          PooledByteBuf<T> buf = newByteBuf(maxCapacity);
       	
          allocate(cache, buf, reqCapacity);
          return buf;
      }
      
      private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, final int reqCapacity) {
          final int normCapacity = normalizeCapacity(reqCapacity);
          // 1.先判断是否为 tiny 或者 small
          if (isTinyOrSmall(normCapacity)) { 
             
              int tableIdx;
              PoolSubpage<T>[] table;
              
              // 2.先尝试从缓存中获取
              // 2.1 如果缓存中分配成功，直接返回
              // 2.2 否则获取双向链表中的索引，进行分配，比如请求为 16B，则从双向链表中索引为0的都是以16B切分的subpage
              final PoolSubpage<T> head = table[tableIdx];
              
               synchronized (head) {
                   // 进行分配（未涉及实际内存的分配，只是标记信息）
                   long handle = s.allocate(); 
                   // 实际的内存分配
                   s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity, cache);
                   incTinySmallAllocation(tiny);
                   return;
               }
              
              synchronized (this) {
                  // 双向循环链表还没初始化，使用normal分配
                  allocateNormal(buf, reqCapacity, normCapacity);
              }
              incTinySmallAllocation(tiny);
              return;
          }
          
          // normal 分配
          if (normCapacity <= chunkSize) {
              synchronized (this) {
                  // 在 chunkList 中寻找一个 chunk 块分配，如果没有找到合适的，则创建一个 chunk 块
                  // 使用伙伴算法进行快速分配，并加入 chunkList
                  allocateNormal(buf, reqCapacity, normCapacity, cache);
                  ++allocationsNormal;
              }
          }
      }
      ```
      
      
  
  - 伙伴分配算法
  
    - 作用：减少外部内存碎片
  
      外部内存碎片：因为所有的内存分配必须居于每页的大小（32 位操作系统中，每页的大小为 4k )，所以假设一个进程需要的内存为 3k，但是也只能分配 4 k 的内存大小（还有 1k 即为碎片）
  
      内部内存碎片：频繁的分配与回收物理页面会导致大量的、连续且小的页面块夹杂在已分配的页面中间，从而产生外部碎片
  
    - 实现原理（简化版理解）
  
      - 将内存块分成大小相等的两个块，分成的两个内存块被称为伙伴块
      - 会将内存分成若干块，使用链表连接，每块包含若干个页
      - 分配时，会向最接近的内存块分配（假设内存大小为 256 ，请求一个 21 大小的内存）会不断的分成两个相同的内存块，直到最接近 21 ，然后分配给进程，所以最后分配给进程的是 32
  
      ![](C:\Users\guangyong.deng\Desktop\博客\Netty\kenel-memory.png)
  
    - 回收也是同样的，如果两个伙伴都没有使用，则合并为更大的一个内存块，从而减少了外部内存碎片的产生
  
    - 代码设计
    
      - 设计方案：主要是需要相应的数据结构能够记录内存块的分配情况以及使用情况，能够快速的找到未使用的内存节点
      - 代码实现：可以查看 Netty - PoolChunk
    
    

