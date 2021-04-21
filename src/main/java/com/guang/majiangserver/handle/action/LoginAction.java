package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.Action;
import com.guang.majiangclient.client.common.Event;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.LoginRequestMessage;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.InfoMapper;
import com.guang.majiangserver.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import redis.clients.jedis.Jedis;

/**
 * @ClassName LoginAction
 * @Description 登陆服务 action
 * @Author guangmingdexin
 * @Date 2021/4/21 14:28
 * @Version 1.0
 **/
@Action
public class LoginAction implements ServerAction<LoginRequestMessage, AuthResponseMessage>{

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, LoginRequestMessage request, AuthResponseMessage response) {
        User user = request.getUser();
        // 首先查看 session 是否存在，如果 session 存在不需要校验数据库，直接进入
        Jedis jedis = JedisUtil.getJedis();
        String tel = user.getTel();
        String key = "session:" + tel;
        String pwd = jedis.get(key);
        if(pwd == null) {
            // 进行账号进行校验
            SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
            User u = mapper.getUserInfoByTel(tel);
            if(u != null && user.getPwd().equals(u.getPwd())) {
                jedis.setex(key, 3600, u.getPwd());
                ResponseUtil.responseBuildFactory(response, u, 200, Event.LOGIN, "登陆成功！", true);
                return;
            }
            ResponseUtil.responseBuildFactory(response, null, 400, Event.LOGIN, "账号或者密码错误!", false);
        }
        // 如果有 session 则通过 session 进行校验

    }
}
