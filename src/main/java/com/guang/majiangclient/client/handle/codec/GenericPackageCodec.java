package com.guang.majiangclient.client.handle.codec;

import com.guang.majiangclient.client.common.GenericMessage;
import com.guang.majiangclient.client.common.MessageType;
import com.guang.majiangclient.client.common.MessageVersion;
import com.guang.majiangclient.client.common.Package;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MaxBytesRecvByteBufAllocator;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName GenericPackageCodec
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/1 14:26
 * @Version 1.0
 **/
public class GenericPackageCodec extends MessageToByteEncoder<GenericMessage> {

    // 头尾字节长度 + 数据包的长度字段
    public static final int TOTALLEN = 2;

    public static final int HEADER_TAIL_LENGTH = 8;

    public static final int MESSAGE_INFO = 6;

    @Override
    protected void encode(ChannelHandlerContext ctx, GenericMessage message, ByteBuf byteBuf) throws Exception {
        // 获取版本号，类型
        Package anno = message.getClass().getAnnotation(Package.class);
        if(anno.version() == MessageVersion.Unknown || anno.type() == MessageType.Unknown) {
            throw new IllegalArgumentException("错误的数据类型！");
        }
        message.setVersion(anno.version().getVersion());
        message.setChannel(anno.type().getType());
        if (message.getVersion() == MessageVersion.V10.getVersion()) {
           // System.out.println("进行编码操作！" + message.getClass());
            encodeV10(ctx, message, byteBuf);
        } else {
            encodeVX(ctx, message, byteBuf);
        }
    }

    private void encodeV10(ChannelHandlerContext ctx, GenericMessage message, ByteBuf byteBuf) {
        String data = message.encoder(message);
        byte[] body = data==null ? new byte[0] : data.getBytes(StandardCharsets.UTF_8);
        message.setLength((short)body.length);
        if(body.length > GenericMessage.PKG_MAX_LENGTH) {
            throw new IllegalArgumentException("数据包数据长度超过最大值！");
        }
        // 获取 注解
        Package anno = message.getClass().getAnnotation(Package.class);

        if(anno == null) {
            throw new IllegalArgumentException("非法的数据包！");
        }
        // 设置数据包的版本 类型
        message.setVersion(anno.version().getVersion());
        message.setChannel(anno.type().getType());
        short totalLen = (short) (HEADER_TAIL_LENGTH + message.getLength() + MESSAGE_INFO);
//        System.out.println("totalLen: " + totalLen);
//        System.out.println("writeIndex: " + byteBuf.writerIndex());
    //    System.out.println(byteBuf.capacity());
        // 默认的 btyeBuf 容量是 256
        if(totalLen > byteBuf.maxCapacity()) {
            throw new IllegalArgumentException("数据包数据长度超过 byteBuf 的最大长度");
        }

       // System.out.println("写入数据！");
        byteBuf.writeShort(totalLen);
        byteBuf.writeBytes(GenericMessage.PKG_PREFIX);
        byteBuf.writeShort(message.getVersion())
                .writeShort(message.getChannel())
                .writeShort(body.length);
        byteBuf.writeBytes(body);
        byteBuf.writeBytes(GenericMessage.PKG_SUFFIX);
        ctx.flush();
    }

    private void encodeVX(ChannelHandlerContext ctx, GenericMessage message, ByteBuf byteBuf) {
        encodeV10(ctx, message, byteBuf);
    }


}
