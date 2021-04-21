package com.guang.majiangserver.mapper;

import com.guang.majiangclient.client.entity.User;

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
}
