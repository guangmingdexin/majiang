package ds.guang.majing.client.event;

import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.room.Room;

import static ds.guang.majing.common.DsConstant.EVENT_POST_HANDCARD_ID;

/**
 * @author guangmingdexin
 */
public class PrepareRequest extends Request {


    public PrepareRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }


    @Override
    protected DsResult after(DsResult result) {
        if(result != null && result.isOk()) {
            System.out.println("准备进入游戏！");
            // 获取房间信息
            System.out.println("room-info:" + result);
            return result;
        }
        System.out.println("room-info:" + result);
        return DsResult.error();
    }

}
