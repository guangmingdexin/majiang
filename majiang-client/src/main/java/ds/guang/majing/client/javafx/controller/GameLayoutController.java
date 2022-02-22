package ds.guang.majing.client.javafx.controller;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.game.ClientFourRoom;
import ds.guang.majing.client.game.ClientMaJiang;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.CardType;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.room.Room;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author guangyong.deng
 * @date 2022-02-21 17:16
 */
public class GameLayoutController implements Initializable {

    @FXML
    Label bottom_username;

    @FXML
    HBox bottom_card;


    private final static String CARD_IMAGE_URL = "image/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // 1.获取各个玩家用户信息（暂时先不做）
        bottom_username.setText("123456");

        ClientFourRoom room =  CacheUtil.getRoom();

        // 2.获取房间信息
        // 3.获取棋牌，生成 MaJiang 对象

        Player[] players = room.getPlayers();

        // 当前玩家
        Player cur = players[room.getCurRoundIndex() % room.getPlayerCount()];

        List<Integer> cards = cur.getCards();


        for (Integer v : cards) {

            // 生成 majiang 对象
            ClientMaJiang card = new ClientMaJiang();

             card.setValue(v);
             card.setCardType(CardType.generate(v));
             card.setSrc(new Image(CARD_IMAGE_URL + getSrcName(v, card.getCardType())));
             card.setView(new ImageView(card.getSrc()));

             room.addSrcCard(card);
             bottom_card.getChildren().add(card.getView());

        }

    }



    private String getSrcName(Integer v, CardType type) {
        return type.getPatten() + (v.toString()).substring(v.toString().length() - 1) + ".png";
    }
}
