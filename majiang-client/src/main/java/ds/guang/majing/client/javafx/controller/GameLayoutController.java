package ds.guang.majing.client.javafx.controller;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.game.CardState;
import ds.guang.majing.client.game.ClientMaJiang;
import ds.guang.majing.client.game.ClientPlayer;
import ds.guang.majing.common.game.card.CardType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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

    @FXML
    FlowPane bottom_out_card;


    public final static String CARD_IMAGE_URL = "image/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // 1.获取各个玩家用户信息（暂时先不做）
        bottom_username.setText("123456");



        // 2.获取房间信息
        // 3.获取棋牌，生成 MaJiang 对象

        // 当前玩家
        ClientPlayer cur = CacheUtil.getPlayer();
        List<Integer> cards = cur.getCards();


        for (Integer v : cards) {

            // 生成 majiang 对象
            ClientMaJiang card = new ClientMaJiang();

             card.setValue(v);
             card.setCardType(CardType.generate(v));
             card.setSrc(new Image(CARD_IMAGE_URL + getSrcName(v, card.getCardType())));
             card.setView(new ImageView(card.getSrc()));
             card.setState(CardState.CARD_INIT);

             // 注册游戏动作

             cur.addSrcCard(card);

             card.onbind(event -> card.onFocusEvent());
             bottom_card.getChildren().add(card.getView());

        }

        // 加一张阻断牌

        ImageView block = new ImageView(new Image(CARD_IMAGE_URL + getSrcName(19, CardType.generate(19))));
        bottom_card.getChildren().add(block);
//        block.setVisible(false);
    }



    private String getSrcName(Integer v, CardType type) {
        return type.getPatten() + (v.toString()).substring(v.toString().length() - 1) + ".png";
    }
}
