package com.guang.majiangclient.client.handle.codec;

import com.guang.majiangclient.client.common.GenericMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName GenericPackageDecodec
 * @Description 消息解码器 接收消息 根据封装的消息包类型进行解码
 * @Author guangmingdexin
 * @Date 2021/4/6 11:27
 * @Version 1.0
 *
 **/
public class GenericPackageDecoder extends ByteToMessageDecoder {

    // 头尾字节长度
    public static final int TOTAL_LEN = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {

        // System.out.println("读取数据包！");
        // 添加一个字段在包头 为该数据包的总长度
        // 首先读取该字段
        // 半包问题 --- 缓冲区数据不足一个数据包

        // 粘包问题 --- 缓冲区数据超过一个数据包
        while (in.isReadable(TOTAL_LEN)) {
            // 该数据包总长度
            // 判断该数据包长度
            // 记录当前读指针位置
            in.markReaderIndex();
            int totalLen = in.readInt();
            if(totalLen > GenericMessage.PKG_MAX_LENGTH || totalLen < 16) {
                throw new IllegalArgumentException("数据包长度值错误！");
            }
            if(!in.isReadable(totalLen)) {
                // 将读指针恢复到以前的位置
                // 准备下一次读取
                in.resetReaderIndex();
                return;
            }
            int readerIndex = in.readerIndex();
            ByteBuf frame = in.retainedSlice(readerIndex, totalLen);
            in.readerIndex(readerIndex + totalLen);

            list.add(frame);
        }
    }
}
