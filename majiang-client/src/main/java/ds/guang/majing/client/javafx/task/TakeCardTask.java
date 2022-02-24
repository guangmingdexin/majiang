package ds.guang.majing.client.javafx.task;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.game.ClientMaJiang;
import ds.guang.majing.client.game.ClientPlayer;
import ds.guang.majing.client.javafx.component.GameLayout;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-02-22 16:39
 */
@AllArgsConstructor
public class TakeCardTask implements Task {

    private final Object lock = new Object();

    private ClientMaJiang card;

    @Override
    public void onBind() {

        card.onbind(event -> {
            card.onFocusEvent();
        });
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void run() {
        onBind();

        System.out.println("摸牌！" + Thread.currentThread().getName());
        HBox hbox = (HBox) GameLayout.scene.lookup("#bottom_card");

        // 有顺序的插入

        // 1.先插入最后一个节点
        synchronized (lock) {
            hbox.getChildren().add(card.getView());
        }

        // 2.然后按顺序插入（先将右边位置同时向右移动，然后插入）

        Platform.runLater(() -> {

            try {

                synchronized (lock) {
                    Thread.sleep(1000);

                    // 计算起始目标，终点目标

                    ClientPlayer cur = CacheUtil.getPlayer();

                    List<ClientMaJiang> srcList = cur.getSrcList();

                    //
                    int index = 0;

                    for (ClientMaJiang c : srcList) {
                        if(c.getValue() >= card.getValue()) {
                            break;
                        }else {
                            index ++;
                        }
                    }

                    // 交换最后
                    System.out.println("index: " + index + " last: " +  card.getValue() + " size: " + hbox.getChildren().size());


                    // 插入到 index 的位置
                    hbox.getChildren().remove(hbox.getChildren().size() - 1);
                    hbox.getChildren().add(index, card.getView());

                    cur.addSrcCard(card);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {

    }
}
