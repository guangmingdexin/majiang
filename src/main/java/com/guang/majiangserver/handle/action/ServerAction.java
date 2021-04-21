package com.guang.majiangserver.handle.action;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 * @Description 服务器业务处理接口 K 请求数据类型， V 返回数据类型
 **/
public interface ServerAction<K, V> {

    void execute(ChannelHandlerContext ctx, ChannelGroup group, K request, V response);


}
