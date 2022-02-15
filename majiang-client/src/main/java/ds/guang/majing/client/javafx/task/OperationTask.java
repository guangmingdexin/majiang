package ds.guang.majing.client.javafx.task;

import ds.guang.majing.client.network.EventRequest;
import ds.guang.majing.client.network.Request;
import ds.guang.majing.common.cache.CacheUtil;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.room.Room;
import ds.guang.majing.common.util.DsConstant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ds.guang.majing.client.Demo.*;
import static ds.guang.majing.common.game.card.MaJiangEvent.*;
import static ds.guang.majing.common.util.DsConstant.*;

/**
 *
 * 单例
 *
 * @author guangyong.deng
 * @date 2022-02-10 9:41
 */
@Getter
@Setter
@Accessors(chain = true)
public class OperationTask implements Task {

    private DsMessage<GameInfoRequest> message;

    private DsResult<GameInfoResponse> eventResult;


    private GameEvent gameEvent;

    private Card card;

    private OperationTask() {}


    private static class OperationTaskHolder {
        private static OperationTask center = new OperationTask();
    }

    public static OperationTask getInstance() {
        return OperationTaskHolder.center;
    }


    @Override
    public void onBind() {

        pong.setVisible(true);
        gang.setVisible(true);
        hu.setVisible(true);
        ignore.setVisible(true);

        Map<MaJiangEvent, Integer> selectEvent = ((MaGameEvent)gameEvent).getSelectEvent();

        if(selectEvent.containsKey(PONG)) {

            System.out.println("pong");
            pong.setOnAction(this);

        }else if(selectEvent.containsKey(IN_DIRECT_HU)
                || selectEvent.containsKey(SELF_HU)) {
            hu.setOnAction(this);
            System.out.println("hu");
        }else if(selectEvent.containsKey(DIRECT_GANG)
                || selectEvent.containsKey(IN_DIRECT_GANG)
                || selectEvent.containsKey(SELF_GANG)) {
            gang.setOnAction(this);
        } else {
            throw new IllegalArgumentException("not found event!");
        }

        ignore.setOnAction(this);
    }

    @Override
    public void onCancel() {

        pong.setOnAction(null);
        pong.setVisible(false);

        gang.setOnAction(null);
        gang.setVisible(false);

        hu.setOnAction(null);
        hu.setVisible(false);

        ignore.setOnAction(null);
        ignore.setVisible(false);
    }

    @Override
    public void run() {
         onBind();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(ActionEvent event) {

        // 从上下文中获取 用户信息
        String userId = CacheUtil.getUserId();

        String eventName;
        MaJiangEvent eventValue;

        MaGameEvent maGameEvent = (MaGameEvent)gameEvent;

        // 获取点击按钮的值
        String value = ((Button)event.getSource()).getText();

        switch (value) {
            case "碰":
                eventName = EVENT_PONG_ID;
                eventValue = PONG;
                break;
            case "胡":
                eventName = EVENT_HU_ID;
                // 从 selectEvent 中选择优先级一致的
               eventValue = findPriority(maGameEvent.getSelectEvent(), IN_DIRECT_HU.getPriority());
                break;
            case "杠":
                eventName = EVENT_GANG_ID;
                eventValue = findPriority(maGameEvent.getSelectEvent(), MaJiangEvent.DIRECT_GANG.getPriority());
                break;
            case "过":
                eventName = DsConstant.EVENT_IGNORE_ID;
                eventValue = MaJiangEvent.NOTHING;
                break;
            default:
                throw new IllegalArgumentException("没有的类型");
        }

        GameInfoRequest infoRequest = new GameInfoRequest();
        MaGameEvent actionEvent = new MaGameEvent()
                .setActionEvent(eventValue)
                .setPlayId(userId);
        DsMessage<GameInfoRequest> message = DsMessage.build(
                eventName,
                userId,
                infoRequest
                        .setEvent(
                                actionEvent)
                        .setCard(card)
                        .setUserId(userId)
        );

        CompletableFuture.runAsync(() -> {

            Request eventRequest = new EventRequest(message, GAME_URL);
            eventResult = eventRequest.execute(null);

            if(eventResult.success()) {

                Platform.runLater(() -> {
                    GameInfoResponse eventResp = eventResult.getData();
                    // 做事件处理
                    Room room = CacheUtil.getRoomById(userId);
                    // 做本地的回合切换
                    room.setCurRoundIndex(eventResp.getCurRoundIndex());

                    room.eventHandler(actionEvent, card.value());
                    System.out.println("......... 做最后的界面渲染........." + eventResp);
                    System.out.println("事件之后： " + room.findPlayerById(userId));

                });
            }

        });

        // 取消事件
        onCancel();

    }


    private MaJiangEvent findPriority(Map<MaJiangEvent, Integer> selectEvent, int p) {

        for (MaJiangEvent e : selectEvent.keySet()) {

            if(e.getPriority() == p) {
                return e;
            }
        }
        return null;
    }


}
