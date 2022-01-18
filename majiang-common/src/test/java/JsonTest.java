import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;

/**
 * @author guangyong.deng
 * @date 2022-01-07 13:36
 */
public class JsonTest {

    public static void main(String[] args) {

       // Test test = new Test("1", "2");

        Object message = DsMessage.build("-1", "-1", DsResult.data("hello"));


        String json = JsonUtil.objToJson(message);

        System.out.println(json);

        DsMessage<DsResult<Test>> o = null;
        try {
            o = JsonUtil.getMapper().readValue(json, new TypeReference<DsMessage<DsResult<Test>>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        // DsMessage<DsResult<Test>> o = (DsMessage<DsResult<Test>>) JsonUtil.stringToObj(json, DsMessage.class);


        System.out.println(o);

        DsResult<Test> data = (DsResult<Test>) o.getData();
        System.out.println(data.getClass());
        System.out.println(data.getData().getClass());
    }


    static class Test {

        private String id;

        private String name;

        public Test() {
        }

        public Test(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public Test setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public Test setName(String name) {
            this.name = name;
            return this;
        }
    }
}
