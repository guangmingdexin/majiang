package ds.guang.majing.client.event;

import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.dto.GameUser;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author guangmingdexin
 */
public class PrepareRequest extends Request {

    private static final String id = DsConstant.EVENT_PREPARE_ID;

    private GameUser gameUser;

    public PrepareRequest(GameUser gameUser) {
        this.gameUser = gameUser;
        this.setHttpClient(HttpClientBuilder.create().build())
                .setHttpPost(new HttpPost("http://localhost:9001/"))
                .setMessage(DsMessage.build(id, "1", gameUser.getUserId()))
                .setEntity(new StringEntity(JsonUtil.objToJson(message), "UTF-8"))
                .getHttpPost()
                .setEntity(getEntity());

        this.getHttpPost().setHeader("Content-Type", "application/json;charset=utf8");
    }

    @Override
    public DsResult call()  {

        DsResult result = super.call();

        if(result != null && result.isOk()) {
            System.out.println("准备进入游戏！");
            return result;
        }

        return DsResult.error();
    }
}
