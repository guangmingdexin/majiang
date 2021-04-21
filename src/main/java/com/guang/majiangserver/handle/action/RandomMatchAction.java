package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.RandomMatchRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

/**
 * @ClassName RandomMatchAction
 * @Description 随机匹配
 * @Author guangmingdexin
 * @Date 2021/4/21 15:23
 * @Version 1.0
 **/
@Action
public class RandomMatchAction implements ServerAction<RandomMatchRequestMessage, AuthResponseMessage>{


    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, RandomMatchRequestMessage request, AuthResponseMessage response) {
        // 1.用户点击随机匹配
        // 2. 客户端发送事件消息，
        // 3. 服务端 发送游戏启动信号消息（进行随机匹配，将其他玩家信息发送给各个客户端）
        // 4. 客户端收到回复（加载本地游戏资源，加载远程信息资源【其他玩家的姓名，头像，位置】）
        // 5. 客户端准备完毕，发送游戏开始信号
        // 6. 当四位玩家 都准备完毕，进入游戏阶段
        // 7. 第一阶段 发牌（服务器将手牌信息发送给客户端，可以多线程进行不需要回复）
        // 8. 第二阶段 摸牌 - 出牌（客户端收到之后需要发送确认信号，再进行下一步）
    }

}
