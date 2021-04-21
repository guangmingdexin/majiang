package com.guang.majiang.layout;

import com.guang.majiang.ai.AiThread;
import com.guang.majiang.ai.Worker;
import com.guang.majiang.common.GlobalConstant;
import com.guang.majiang.common.SpecialEvent;
import com.guang.majiang.image.CardFaceImage;
import com.guang.majiang.image.CardImage;
import com.guang.majiang.image.MyImage;
import com.guang.majiang.player.Player;
import com.guang.majiang.player.PlayerNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
        // 需要加载 108 张棋牌 108 张 左 右 上
        CardFaceImage faceImage = init.addFaceDown();
        // 获取中心位置
        faceImage.getImageView().setX(GlobalConstant.MIDDLEX);
        faceImage.getImageView().setY(GlobalConstant.MIDDLEY);
        pane.getChildren().add(faceImage.getImageView());
        // 加载事件位置
        List<ImageView> eventImages = init.loadSpecialImage("pong.png", "kong.png", "hu.png", "ignore.png");
        pane.getChildren().addAll(eventImages);

        ImageView arrow = init.loadArrow("arrow.png");
        pane.getChildren().add(arrow);

        LinkedList<CardImage> totalCards = init.addCards();
        LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        PlayerNode<Player> node = init.addPlayerCard();
        ReentrantLock lock = new ReentrantLock();
        AtomicReference<SpecialEvent> state = new AtomicReference<>(null);
        // 启动消费者线程
        Worker worker = new Worker(taskQueue);
        worker.start();

        Condition[] conditions = new Condition[4];
        AiThread[] aiThreads = new AiThread[4];

        for (int i = 0; i < 4; i++) {
            conditions[i] = lock.newCondition();
        }

        for (int i = 0; i < 4; i++) {
            aiThreads[i] = new AiThread(node, taskQueue, lock, conditions[i],
                    conditions[(i + 1) % 4], pane, totalCards,
                    new LinkedBlockingQueue<>(), eventImages, state);
            aiThreads[i].start();
            node = node.next;
        }

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);

        primaryStage.setTitle("麻将小游戏");
        primaryStage.show();

    }
}
