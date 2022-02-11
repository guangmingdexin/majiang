package ds.guang.majing.client.javafx.task;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 *
 * ui 任务
 *
 * @author guangyong.deng
 * @date 2022-02-10 10:41
 */
public interface Task extends Runnable, EventHandler<ActionEvent> {


    /**
     *
     * 绑定相应事件
     *
     */
    void onBind();


    /**
     * 取消事件
     */
    void onCancel();
}
