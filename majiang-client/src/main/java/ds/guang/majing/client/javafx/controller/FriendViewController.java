package ds.guang.majing.client.javafx.controller;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.javafx.component.AvatarFx;
import ds.guang.majing.client.network.idle.WorkState;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.dto.vo.Friend;
import ds.guang.majing.client.remote.dto.vo.FriendVo;
import ds.guang.majing.client.remote.service.IUserService;
import ds.guang.majing.client.remote.service.UserService;
import ds.guang.majing.common.game.message.DsResult;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author guangyong.deng
 * @date 2022-02-25 14:53
 */
public class FriendViewController implements Initializable {


    @FXML
    ListView friend_list_view;

    private final Stage stage;

    private final WorkState workState;

    public FriendViewController(WorkState workState) throws IOException {

        stage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("friend_menu.fxml"));

        // Set this class as the controller
        loader.setController(this);
        stage.setScene(new Scene(loader.load()));

        this.workState = workState;
    }

    public void showStage() {
        stage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        friend_list_view.setCellFactory(listView -> new ListCell<Friend>() {

            @Override
            public void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView view = (ImageView) AvatarFx.build();
                    setText(friend.getFriend().getUsername());
                    setGraphic(view);
                    setId(friend.getId());
                }
            }
        });

        MultipleSelectionModel model = friend_list_view.getSelectionModel();
        ReadOnlyObjectProperty property = model.selectedItemProperty();
        property.addListener(listener -> {
            // 1.是否已经打开对应玩家的界面，如果已经打开，则

            ChatViewController chatViewController = new ChatViewController((Friend) model.getSelectedItem(), workState);
            chatViewController.show();

        });

        IUserService userService = new UserService();
        DsResult<FriendVo> resp = userService.getFriends(new UserQueryAo(CacheUtil.getUserId()));

        if(resp.success()) {

            FriendVo friends = resp.getData();

            friends.getFriends().forEach(f -> {
                friend_list_view.getItems().add(f);
            });

        }
    }
}
