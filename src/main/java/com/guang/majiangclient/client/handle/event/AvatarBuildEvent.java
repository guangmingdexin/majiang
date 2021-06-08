package com.guang.majiangclient.client.handle.event;

import com.guang.majiangclient.client.cache.CacheUtil;
import com.guang.majiangclient.client.common.annotation.RunnableEvent;
import com.guang.majiangclient.client.common.enums.Direction;
import com.guang.majiangclient.client.common.enums.Event;
import com.guang.majiangclient.client.entity.Avatar;
import com.guang.majiangclient.client.entity.GameUser;
import com.guang.majiangclient.client.entity.Room;
import com.guang.majiangclient.client.layout.ClientLayout;
import com.guang.majiangclient.client.util.ImageUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @ClassName AvatarBuildEvent
 * @Author guangmingdexin
 * @Date 2021/5/29 9:09
 * @Version 1.0
 **/
@RunnableEvent(value = Event.UIEVENT)
@NoArgsConstructor
@AllArgsConstructor
public class AvatarBuildEvent implements Runnable {

    private Room room;

    @Override
    public void run() {
        HashSet<GameUser> players = room.getPlayers();

        HashMap<Direction, Avatar> avatars = new HashMap<>(4);
        Direction cur = null;
        for (GameUser gameUser : players) {
            if (gameUser.getUserId() == CacheUtil.getUserInfo().getUserId()) {
                cur = gameUser.getDirection();
            }
            String base64 = gameUser.getBase64();
            byte[] bytes = ImageUtil.decoderBase64(base64);
            ImageView view = ImageUtil.bytesConvertToImage(bytes, 80, 80, 0);
            ImageView viewLeft = ImageUtil.bytesConvertToImage(bytes,80, 80, 90);
            ImageView viewRight = ImageUtil.bytesConvertToImage(bytes, 80, 80, 0);
            ImageView viewTop = ImageUtil.bytesConvertToImage(bytes, 80, 80, 270);
            Avatar avatar = new Avatar(base64, bytes, view, viewLeft, viewRight, viewTop,
                    gameUser.getScore());
            avatars.put(gameUser.getDirection(), avatar);
        }

        //
        Direction rightDir = Direction.valueOf((cur.getDirection() + 1) % 4);
        Direction topDir = Direction.valueOf((cur.getDirection() + 2) % 4);
        Direction leftDir = Direction.valueOf((cur.getDirection() + 3) % 4);

        Label bottom = new Label("0", avatars.get(cur).getImage());
        bottom.setContentDisplay(ContentDisplay.TOP);

        Label right = new Label("0", avatars.get(rightDir).getImageRight());
        right.setContentDisplay(ContentDisplay.TOP);

        Label left = new Label("0", avatars.get(leftDir).getImageRight());
        right.setContentDisplay(ContentDisplay.TOP);

        Label top = new Label("0", avatars.get(topDir).getImageRight());
        right.setContentDisplay(ContentDisplay.TOP);

        right.setRotate(270);
        left.setRotate(90);
        top.setRotate(180);

        ClientLayout.hBottom.setPrefSize(100, 50);
        ClientLayout.hBottom.getChildren().add(bottom);
        AnchorPane.setLeftAnchor(bottom, 200d);

        ClientLayout.vRight.setPrefSize(50, 100);
        ClientLayout.vRight.getChildren().add(right);
        AnchorPane.setRightAnchor(right, 20d);
        AnchorPane.setBottomAnchor(right, 10d);

        ClientLayout.vLeft.setPrefSize(50, 100);
        ClientLayout.vLeft.getChildren().add(left);
        AnchorPane.setLeftAnchor(left, 20d);
        AnchorPane.setTopAnchor(left, 50d);

        ClientLayout.hTop.setPrefSize(100, 50);
        ClientLayout.hTop.getChildren().add(top);
        AnchorPane.setRightAnchor(top, 200d);

    }
}
