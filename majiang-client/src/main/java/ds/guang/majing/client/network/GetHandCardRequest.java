package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ds.guang.majing.common.DsConstant.EVENT_GET_HANDCARD_ID;

/**
 * @author asus
 */
public class GetHandCardRequest extends Request {

    public GetHandCardRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<List<Integer>>> message = null;

        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<List<Integer>>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        return message.getData();
    }


}
