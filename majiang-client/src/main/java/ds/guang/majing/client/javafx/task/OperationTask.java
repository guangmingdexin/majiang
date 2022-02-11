package ds.guang.majing.client.javafx.task;

import ds.guang.majing.client.network.EventRequest;
import ds.guang.majing.client.network.Request;
import ds.guang.majing.client.rule.platform.CacheUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.card.Card;
import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.MaGameEvent;
import ds.guang.majing.common.game.card.MaJiangEvent;
import ds.guang.majing.common.game.dto.GameUser;
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
import static ds.guang.majing.common.util.DsConstant.GAME_URL;

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

        if(selectEvent.containsKey(MaJiangEvent.PONG)) {

            System.out.println("pong");
            pong.setOnAction(this);

        }else if(selectEvent.containsKey(MaJiangEvent.IN_DIRECT_HU)) {
            hu.setOnAction(this);
            System.out.println("hu");
        }else {
            throw new IllegalArgumentException("not found event!");
        }
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
                eventName = DsConstant.EVENT_PONG_ID;
                eventValue = MaJiangEvent.PONG;
                break;
            case "胡":
                eventName = DsConstant.EVENT_HU_ID;
                // 从 selectEvent 中选择优先级一致的
               eventValue = findPriority(maGameEvent.getSelectEvent(), MaJiangEvent.IN_DIRECT_HU.getPriority());
                break;
            case "杠":
                eventName = DsConstant.EVENT_GANG_ID;
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
        DsMessage<GameInfoRequest> message = DsMessage.build(
                eventName,
                userId,
                infoRequest
                        .setEvent(
                            new MaGameEvent()
                                .setActionEvent(eventValue)
                                .setPlayId(userId))
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
                    room.eventHandler(userId, eventValue.getValue(), card.value());
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
