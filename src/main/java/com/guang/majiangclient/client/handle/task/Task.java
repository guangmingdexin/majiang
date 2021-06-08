package com.guang.majiangclient.client.handle.task;

import com.guang.majiangclient.client.common.enums.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName Task
 * @Description 作为 客户端 与 服务处理中心的数据载体
 * @Author guangmingdexin
 * @Date 2021/4/17 14:36
 * @Version 1.0
 **/
@Getter
@Setter
public class Task<T> {

    private Event event;

    // 需要发送的数据类型
    private T data;

    public Task(Event event, T data) {
        this.event = event;
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj) {
            return true;
        }

        if(obj instanceof Task) {
            return this.event == ((Task) obj).event;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return event.hashCode() + data.hashCode();
    }
}
