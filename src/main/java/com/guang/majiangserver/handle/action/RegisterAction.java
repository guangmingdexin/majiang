package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.entity.AuthResponse;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.RegisterRequestMessage;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.InfoMapper;
import com.guang.majiangserver.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * @ClassName RegisterAction
 * @Description 服务端处理注册 action
 * @Author guangmingdexin
 * @Date 2021/4/18 8:58
 * @Version 1.0
 **/
@Action
public class RegisterAction implements ServerAction<RegisterRequestMessage, AuthResponseMessage> {

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, RegisterRequestMessage request, AuthResponseMessage response) {
        System.out.println("执行注册业务！");

        User user = request.getUser();
        // 先向redis 缓存查询有无此人
        Jedis jedis = JedisUtil.getJedis();
        String s = jedis.get(user.getTel());

        if(s != null) {
            ResponseUtil.responseBuildFactory(response, null, 400, Event.REGISTER, "该用户已存在", false);
            ctx.writeAndFlush(response);
        }else {
            SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
            User u = mapper.getUserInfoByTel(user.getTel());
            if(u == null && (mapper.register(user) == 1)) {
                // 注册成功！
                // 0 表示还未进入游戏
                // 1 表示正在游戏
                jedis.set(user.getTel(), String.valueOf(0));
                ResponseUtil.responseBuildFactory(response, null, 200, Event.REGISTER, "注册成功", true);
                ctx.writeAndFlush(response);
            }else {
                ResponseUtil.responseBuildFactory(response, null, 400, Event.REGISTER, "注册失败", false);
                ctx.writeAndFlush(response);
            }
        }
    }
}
