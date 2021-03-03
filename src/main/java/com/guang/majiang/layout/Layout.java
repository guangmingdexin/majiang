package com.guang.majiang.layout;

import com.guang.majiang.ai.AITask;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.image.CardFaceImage;
import com.guang.majiang.image.MyImage;
import com.guang.majiang.player.PlayerCard;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guangmingdexin
 */
public class Layout extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Pane pane = new Pane();

        SimpleInit init = new SimpleInit();

        // 1.加载背景图片
        MyImage bg = init.getBg();

        pane.getChildren().add(bg.getImageView());

        PlayerCard[] playerCards = init.addPlayerCard();

        List<Node> nodes = new ArrayList<>();

        List<Node> others = new ArrayList<>();

        Map<String, int[]> index = new HashMap<>(4);

        for (PlayerCard playerCard : playerCards) {
            // 如何做出动画效果
            // 界面渲染只能单线程
            // 4 个线程每次获取 4 张牌
            // 并通过一个线程汇总 最后加入 pane
            Map<String, List<Node>> handCard = UIInit.initHandCard(playerCard, false);
            List<Node> cards = handCard.get("neg");
            List<Node> other = handCard.get("pos");

            index.put(playerCard.getPlayer().getId(), new int[]{nodes.size(), nodes.size() + cards.size() - 1});
            others.addAll(other);
            nodes.addAll(cards);

        }

        pane.getChildren().addAll(others);


        EventHandler<ActionEvent> eventHandler = e -> {

            while (true) {

                for (Map.Entry<String, int[]> card : index.entrySet()) {

                    int[] v = card.getValue();

                    if(v[0] + 3 <= v[1]) {

                        for (int j = v[0]; j <= v[0] +3 ; j++) {
                            pane.getChildren().add(nodes.get(j));
                        }

                        v[0] += 4;

                    }else if(v[0] <= v[1]) {

                        for (int j = v[0]; j <= v[1] ; j++) {
                            pane.getChildren().add(nodes.get(j));
                        }

                        v[0] = v[1] + 1;

                    }

                    index.put(card.getKey(), v);
                }

                break;
            }

        };

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(5);
        animation.play();

        // 整理玩家手牌
        // 需要重新计算玩家手牌 的 x, y
        // 定义一个排序算法
        // 排序完成之后，重新计算x,y

        EventHandler<ActionEvent> sortOutCard = e -> {

            // 删除所有节点
            pane.getChildren().removeAll(nodes);
            nodes.clear();

            for (PlayerCard playerCard : playerCards) {
                playerCard.getCards().sort((m1, m2) -> m1.getValue() - m2.getValue());
                Map<String, List<Node>> handCard = UIInit.initHandCard(playerCard, true);
                List<Node> cards = handCard.get("neg");
                nodes.addAll(cards);
            }

            pane.getChildren().addAll(nodes);

            // 开始打牌
            // 启动事件
            // 轮流出牌
            AITask aiTask = new AITask(playerCards);
            aiTask.start();

            System.out.println(Thread.currentThread().getName());
        };

        animation.setOnFinished(sortOutCard);
        // 加载剩余牌数
        CardFaceImage faceImage = init.addFaceDown();
        // 获取中心位置

        faceImage.getImageView().setX(GlobalConstant.MIDDLEX);
        faceImage.getImageView().setY(GlobalConstant.MIDDLEY);
        pane.getChildren().add(faceImage.getImageView());

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);

        primaryStage.setTitle("麻将小游戏");
        primaryStage.show();


        // 2.
    }



}
