package ds.guang.majing.client.network;

import ds.guang.majing.common.DsResult;

/**
 * 匹配玩家
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

        if(result.isOk()) {
            System.out.println("准备进入游戏！");
            // 获取房间信息
            System.out.println("room-info:" + result);
            return result;
        }
        System.out.println("result: " + result);
        return result;
    }

}
