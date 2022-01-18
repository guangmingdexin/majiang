package ds.guang.majing.client.javafx.component;

import ds.guang.majing.common.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * @author guangyong.deng
 * @date 2021-12-08 16:02
 */
public class DsButton extends Button {

    public DsButton(String text) {
        super(text);
    }

        // 图形渲染

        // 业务动作
        // 产生一个问题，有多个不同事件 比如出牌事件发生 如何自动对应到相应的行为
        // 同样如果有返回值，如何利用好返回值
        // 比如 登录之后 服务器需要返回一个 token 类似的，后续服务又如何处理

        // 一: 建立一个 Action 处理工厂 当程序初始化时将所有存在的 Action 注册到工厂中
        //      并对每一个事件建立相应的处理 Action
        //      优点：便于扩展 后续可以方便的添加 Action 以及对应的事件
        //      缺点：类非常多，以及对工厂进行管理

        // 二：策略模式
        // 在使用的时候直接传入 父接口，根据多态特性调用相应的 处理方法


    public void setOnAction(EventHandler<ActionEvent> value, Event<String> event) {
            super.setOnAction(value);
            // 1.获取工厂
            // 2.调用
    }
}
