package com.guang.majiangserver.handle.action;

import com.guang.majiangclient.client.common.annotation.Action;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.User;
import com.guang.majiangclient.client.message.AuthResponseMessage;
import com.guang.majiangclient.client.message.FriendRequestMessage;
import com.guang.majiangclient.client.util.JedisUtil;
import com.guang.majiangserver.config.ConfigOperation;
import com.guang.majiangserver.mapper.GameInfoMapper;
import com.guang.majiangserver.mapper.InfoMapper;
import com.guang.majiangserver.util.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName FriendAction
 * @Author guangmingdexin
 * @Date 2021/6/17 11:39
 * @Version 1.0
 **/
@Action
public class FriendAction implements ServerAction<FriendRequestMessage, AuthResponseMessage> {

    @Override
    public void execute(ChannelHandlerContext ctx, ChannelGroup group, FriendRequestMessage request, AuthResponseMessage response) {

        Friend friend = request.getFriend();

        SqlSessionFactory sqlSessionFactory = ConfigOperation.getSqlSessionFactory();
        // mybatis 默认事务手动提交
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        GameInfoMapper mapper = sqlSession.getMapper(GameInfoMapper.class);
        // 1.判断 Redis 中是否存在
        String key = "score:" + friend.getId();

        // 首先判断请求时间类型
        if("get".equals(friend.getType())){
            get(friend, key, mapper);
        }else if("add".equals(friend.getType())){
           add(friend, key, sqlSession, mapper);
        }
        ResponseUtil.responseBuildFactory(response, friend, 200, Event.FRIEND, "操作成功", true);
        ctx.writeAndFlush(response);
    }


    private void get(Friend friend, String key, GameInfoMapper mapper) {
        List<User> friends;
        if(JedisUtil.exist(key)) {
            System.out.println("redis 缓存查询!");
            friends = new ArrayList<>();
            Set<String> zrevrange = JedisUtil.zrevrange(key, 0, -1);
            // 根据 id 顺序查询所有玩家信息
            if(zrevrange != null) {
                zrevrange.forEach(s -> {
                    long uid = Long.parseLong(s);
                    User u = mapper.getUserInfoById(uid);
                    friends.add(u);
                });
                friend.setFriends(friends);
            }
        }else {
            System.out.println("进行数据库查询!：" + friend);
            // 调用
            friends = mapper.getAllFriendsById(friend.getId());
            if (friends != null) {
                // 拼装 Friend 对象
                friend.setFriends(friends);
                // Redis 内存中不存在缓存,直接重写设置缓存
                friends.forEach(user -> {
                    JedisUtil.zadd(key, user.getScore(), String.valueOf(user.getUserId()));
                });
            }
        }
    }

    private void add(Friend friend, String key, SqlSession sqlSession, GameInfoMapper mapper) {
        String tel = friend.getTel();

        InfoMapper infoMapper = sqlSession.getMapper(InfoMapper.class);
        User u = infoMapper.getUserInfoByTel(tel);
        if(u == null || friend.getId() == u.getUserId()) {
            return;
        }
        // 首先判断是否重复添加
        int count = mapper.getFriendById(friend.getId(), u.getUserId());
        if(count == 0) {
            try {
                mapper.addFriendInfo(friend.getId(), u.getUserId());
                sqlSession.commit();
                JedisUtil.zadd(key, u.getScore(), String.valueOf(u.getUserId()));
            }catch (Exception e) {
                sqlSession.rollback();
            }finally {
                sqlSession.close();
            }
            // 查询
            get(friend, key, mapper);
        }
    }
}
