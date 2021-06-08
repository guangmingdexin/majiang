package com.guang.majiangclient.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guang.majiangclient.client.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName User
 * @Description
 * @Author guangmingdexin
 * @Date 2021/3/23 9:19
 * @Version 1.0
 **/
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private long userId;

    private String userName;

    private String tel;

    private String pwd;

    // ImageView 无法被正确反序列化
    // 原因未知
    private Avatar avatar;


    public User() {

    }


    public User(String tel, String pwd) {
        this.tel = tel;
        this.pwd = pwd;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", tel='" + tel + '\'' +
                ", pwd='" + pwd + '\'' +
                ", avatar=" + avatar +
                '}';
    }
}
