package ds.guang.majing.client.game;

import ds.guang.majing.client.cache.CacheUtil;
import ds.guang.majing.client.javafx.component.GameLayout;
import ds.guang.majing.client.javafx.task.TakeCardTask;
import ds.guang.majing.common.game.card.CardType;
import ds.guang.majing.common.game.card.MaJiang;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static ds.guang.majing.client.javafx.controller.GameLayoutController.CARD_IMAGE_URL;

/**
 * @author guangyong.deng
 * @date 2022-02-22 10:21
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class ClientMaJiang extends MaJiang {

    /**
     * 随机标识符
     */
    private String id = UUID.randomUUID().toString().substring(0, 9);

    private Image src;


    private ImageView view;


    /**
     * 是否选中该元素
     */
    private boolean isFocus;

    /**
     * 玩家手术卡牌状态
     */
    private CardState state;



    /**
     * @param event 鼠标时间
     */
    public void onbind(EventHandler<MouseEvent> event) {
        view.setOnMouseClicked(event);
    }

    /**
     * 取消事件
     */
    public void onCancel() {
        view.setOnMouseClicked(null);
    }


    public void onFocusEvent() {

        // 将其他 focus 状态的手牌回归位置
        ClientFourRoom room = CacheUtil.getRoom();

        if(!isFocus && state == CardState.CARD_INIT) {
            // 上移
            double layoutY = view.getTranslateY();
            view.setTranslateY(layoutY - 20);

            setState(CardState.CARD_FOCUS);
            setFocus(true);

            ClientPlayer cur = CacheUtil.getPlayer();
            List<ClientMaJiang> srcList = cur.getSrcList();
            srcList.forEach(card -> {

                if(card != this) {
                    card.onCancelFocusEvent();
                }
            });
        }else if(isFocus && state == CardState.CARD_FOCUS) {
           // 说明玩家希望出牌
            if(room.isCurAround(CacheUtil.getUserId())) {
                // 发起出牌任务
                onTakeOutEvent();

            }
        }

    }


    public void onCancelFocusEvent() {

        if(isFocus) {
            System.out.println("取消选中");
            isFocus = false;
            double layoutY = view.getTranslateY();
            view.setTranslateY(layoutY + 20);
            setState(CardState.CARD_INIT);
        }
    }


    public void onTakeOutEvent() {
        System.out.println("出牌！");
        isFocus = false;
        state = CardState.CARD_OUT;

        ClientPlayer cur = CacheUtil.getPlayer();

        // 重写
        cur.getSrcList().remove(this);
        cur.getOutList().add(this);

//        bottom_out_card.getChildren().add(view);

//        FlowPane flowPane = (FlowPane)GameLayout.scene.lookup("#bottom_out_card");
//
//        flowPane.getChildren().add(view);
//
//        int r = new Random().nextInt(8) + 11;
//
//        ClientMaJiang card = new ClientMaJiang();
//
//        card.setView(new ImageView(new Image(CARD_IMAGE_URL + "dot" + (r % 10) + ".png")));
//        card.setValue(r);
//        card.setCardType(CardType.generate(r));
//
//        Platform.runLater(new TakeCardTask(card));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClientMaJiang that = (ClientMaJiang) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
