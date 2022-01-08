package ds.guang.majing.client.network;

import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.state.Result;

import static ds.guang.majing.common.DsConstant.EVENT_POST_HANDCARD_ID;

/**
 * @author asus
 */
public class PostHandCardRequest extends Request {

    public PostHandCardRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {

//        if( result != null && result.success()) {
//            // 提交一个 ui 任务给 ui 线程
//            System.out.println("渲染其他玩家图像和加载自己的手牌！");
//        }
//
//        return result;
        String[] filedNames = new String[] {"serviceNo"};
        Object[] values = new Object[] {EVENT_POST_HANDCARD_ID};
        DsMessage dsMessage = DsMessage.copy((DsMessage) message, filedNames, values);
        return DsResult.empty();
    }


}
