package com.guang.majiangclient.client.handle.codec;

import com.guang.majiangclient.client.common.GenericMessage;
import com.guang.majiangclient.client.common.MessageFactory;
import com.guang.majiangclient.client.common.enums.MessageVersion;
import com.guang.majiangclient.client.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName GenericPakageClassCodec
 * @Description 将 byteBuf 转换成对应的 javabean 需借助于 Factory
 * @Author guangmingdexin
 * @Date 2021/4/7 10:13
 * @Version 1.0
 **/
public class GenericPackageClassDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 读取包头
        byte[] prefix = new byte[GenericMessage.PKG_PREFIX.length];
        byte[] suffix = new byte[GenericMessage.PKG_SUFFIX.length];
        in.readBytes(prefix);
        if(!Arrays.equals(prefix, GenericMessage.PKG_PREFIX)) {
            throw new IllegalArgumentException("数据包格式不对！");
        }
        // 读取版本
        short v = in.readShort();
        // 读取类型
        short t = in.readShort();
        // 读取数据包长度
        int l = in.readInt();
        if(l < 0 || l > GenericMessage.PKG_MAX_LENGTH) {
            throw new IllegalArgumentException("数据包数据超过最大长度！");
        }
        byte[] data = new byte[l];
        in.readBytes(data);
        // 读取包尾
        in.readBytes(suffix);
        if(!Arrays.equals(suffix, GenericMessage.PKG_SUFFIX)) {
            throw new IllegalArgumentException("数据包格式不对！");
        }
        if(MessageVersion.V10.getVersion() == v) {
            // 构造一个 class 对象
            Class<?> clazz = MessageFactory.getClass(v, t);
            if(clazz == null) {
                // TODO 未知类型 特殊处理
                System.out.println("v: " + v + " t: " + t);
                System.out.println("未知的类型！");
            }else {
                decodeV10(out, data, clazz);
            }
        }
    }


    private void decodeV10(List<Object> out, byte[] data, Class<?> clazz) {
        // 获取消息包携带的 bean 对象
        Object o = JsonUtil.byteToObj(data, clazz);
        // 还需要将整个消息包 转换为对象
        // 通过反射 调用 decode 方法
        // 给所有属性赋值
        out.add(o);
    }
}
