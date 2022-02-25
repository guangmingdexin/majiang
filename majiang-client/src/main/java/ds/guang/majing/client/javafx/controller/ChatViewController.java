package ds.guang.majing.client.javafx.controller;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.javafx.component.AvatarFx;
import ds.guang.majing.client.network.Request;
import ds.guang.majing.client.network.idle.WorkState;
import ds.guang.majing.client.remote.dto.vo.Friend;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ds.guang.majing.common.util.DsConstant.EVENT_CHART;

/**
 * @author guangyong.deng
 * @date 2022-02-25 11:41
 */
public class ChatViewController implements Initializable {

    @FXML
    ListView chat_view_list;

    @FXML
    TextField chat_text;

    private final Stage stage;

    private final Friend friend;

    private final WorkState workState;

    public ChatViewController(Friend friend, WorkState workState) {

        stage = new Stage();
        this.friend = friend;
        this.workState = workState;

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("chat.fxml"));
        // Set this class as the controller
        loader.setController(this);
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chat_view_list.setCellFactory(listView -> new ListCell<String>() {

            @Override
            public void updateItem(String friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView view = (ImageView) AvatarFx.build();
                    setText(friend);
                    setGraphic(view);
                }
            }
        });


        chat_text.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {

                chat_view_list.getItems().add(chat_text.getText());


                // 1.获取好友 id （玩家点击跳转时，
                workState.runAsync(() -> {
                    System.out.println("发送消息！");
                    Request r = new Request(DsMessage.build(EVENT_CHART,
                            CacheUtil.getUserId(),
                            new GameInfoRequest()
                                    .setData(chat_text.getText())
                                    .setFriendId(friend.getFriend().getUserId()))) {

                        @Override
                        protected void before(Runnable task) {}

                        @Override
                        protected DsResult after(String content) {
                            return null;
                        }
                    };

                    DsResult execute = r.execute(null);

                });

                synchronized (WorkState.LOCK) {
                    // 唤醒业务线程
                    WorkState.LOCK.notifyAll();
                }

                chat_text.clear();
            }
        });

       // chat_view_list.getItems().add("hello");
    }


    public void show() {

        stage.showAndWait();
    }
}
