package com.guang.majiangclient.client.entity;

import lombok.*;

import java.util.List;

/**
 * @ClassName Friend
 * @Author guangmingdexin
 * @Date 2021/6/17 10:57
 * @Version 1.0
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friend {

    private long id;

    private String tel;

    private List<User> friends;

    /**
     * get
     * add
     * update
     * delete
     */
    private String type;

    public Friend(long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Friend(long id, String tel, String type) {
        this.id = id;
        this.tel = tel;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", friends=" + friends +
                '}';
    }
}
