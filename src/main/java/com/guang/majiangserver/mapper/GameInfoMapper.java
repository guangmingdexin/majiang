package com.guang.majiangserver.mapper;

import com.guang.majiangclient.client.entity.Friend;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface GameInfoMapper {

    /**
     * 添加玩家游戏信息
     *
     * @param gameUser 玩家游戏信息
     * @return 影响行数
     */
    int addGameInfo(GameUser gameUser);

    /**
     * 获取用户所有好友信息
     *
     * @param userId 用户 id
     * @return Friend
     */
    List<User> getAllFriendsById(long userId);

    /**
     * 添加好友信息
     *
     * @param userId 用户 id
     * @param friendId 好友 id
     * @return 行数
     */
    int addFriendInfo(@Param("userId") long userId, @Param("friendId") long friendId);

    /**
     * 判断某个用户的某个朋友是否存在
     *
     * @param userId 用户 Id
     * @param friendId 朋友 Id
     * @return 记录数
     */
    int getFriendById(@Param("userId") long userId, @Param("friendId") long friendId);


    /**
     * 用户信息
     *
     * @param userId 用户 id
     * @return User
     */
    User getUserInfoById(long userId);
}
