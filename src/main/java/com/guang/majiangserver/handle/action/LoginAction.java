package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.Avatar;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.LoginRequestMessage;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.InfoMapper;
import com.guang.majiangserver.util.ResponseUtil;
import com.guang.majiangserver.util.ServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

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
        String tel = user.getTel();
        // 进行账号进行校验
        SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
        User u = mapper.getUserInfoByTel(tel);
        if(u != null && user.getPwd().equals(u.getPwd())) {

            Avatar avatar = u.getAvatar();
            String key = "avatar:" + u.getUserId();
            String base64 = ServerCache.getAvatar(key, avatar.getPath());
            avatar.setBase64(base64);
            ResponseUtil.responseBuildFactory(response, u, 200, Event.LOGIN, "登陆成功！", true);
            ctx.writeAndFlush(response);
            return;
        }
        ResponseUtil.responseBuildFactory(response, null, 400, Event.LOGIN, "账号或者密码错误!", false);
        ctx.writeAndFlush(response);
    }
}
