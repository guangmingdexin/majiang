package com.guang.majiang.event;

import com.guang.majiang.common.SpecialEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * @ClassName IgnoreHandler
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/3/19 9:25
 * @Version 1.0
 **/
public class IgnoreHandler implements EventHandler<MouseEvent> {

    private SpecialEventTask task;

    public IgnoreHandler(SpecialEventTask task) {
        this.task = task;
    }

    @Override
    public void handle(MouseEvent event) {
        for (ImageView eventImage : task.eventImages) {
            eventImage.setVisible(false);
            eventImage.setOnMouseClicked(null);
        }
        task.event = SpecialEvent.IGNORE;
        task.over = true;
    }
}
