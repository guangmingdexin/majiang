package com.guang.majiangserver.mapper;

import com.guang.majiangclient.client.entity.Avatar;
import com.guang.majiangclient.client.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName InfoMapper
 * @Description
 * @Author guangmingdexin
 * @Date 2021/4/20 17:44
 * @Version 1.0
 **/
public interface InfoMapper {

    /**
     * 根据用户电话返回用户信息
     *
     * @param tel 用户电话信息
     * @return 用户信息
     */
    User getUserInfoByTel(String tel);

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 影响的行数
     */
    int register(User user);

    /**
     * 插入头像
     *
     * @param avatar
     * @return
     */
    int insertAvatar(Avatar avatar);

    /**
     * 获取用户头像的路径
     *
     * @param uid 用户 id
     * @return 用户头像路径
     */
    String getAvatarPath(long uid);

    /**
     * 返回所有用户
     *
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 增加用户分数 score
     *
     * @param uid 用户 id
     * @param score 分数
     * @return 行数
     */
    int incrScore(@Param("uid") long uid, @Param("score") int score);

    /**
     * 减少用户分数 score
     *
     * @param uid 用户 id
     * @param score 分数
     * @return 行数
     */
    int decrScore(@Param("uid") long uid, @Param("score") int score);


    /**
     *
     * 修改用户分数
     *
     * @param uid 用户 id
     * @param score 分数
     * @return 行数
     */
    int updateScore(@Param("uid") long uid, @Param("score") int score);

    /**
     * 获取用户分数
     *
     * @param uid 用户 id
     * @return 分数
     */
    int getInfoScore(long uid);
}
