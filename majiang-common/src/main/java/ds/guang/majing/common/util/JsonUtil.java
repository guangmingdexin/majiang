package ds.guang.majing.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdConverter;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.game.player.ServerPlayer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * 操作 Json 工具类
 *
 * @author guangmingdexin
 * @date 2021/4/1 9:40
 * @version 1.0
 **/
public final class JsonUtil {

    private JsonUtil() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    static {

        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);

        mapper.registerModule(module);

    }

    public static String objToJson(Object obj) {

        Objects.requireNonNull(obj, "null don't convert to json");
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.out.println("转化成 json 字符串失败！");
            e.printStackTrace();
        }
        return null;
    }

    public static Object stringToObj(String s, Class<?> clazz) {

        try {
            return mapper.readValue(s.getBytes(), clazz);
        } catch (IOException e) {
            System.out.println("json 字符串转换对象失败！");
            e.printStackTrace();
        }
        return null;
    }



    public static Map stringToMap(String s) {
        try {
            return mapper.readValue(s.getBytes(), Map.class);
        } catch (IOException e) {
            System.out.println("json 字符串转换对象失败！");
            e.printStackTrace();
        }
        return new HashMap();
    }

    public static Object byteToObj(byte[] data, Class<?> clazz) {

        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object mapToObj(Object data, Class<?> clazz) {
        return mapper.convertValue(data, clazz);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static Class<? extends StdConverter> getConverter() {
        Converter converter = new Converter();
        return converter.getClass();
    }

    static final class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        final static LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

        LocalDateTimeSerializer() {}

        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    static final class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        final static LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

        LocalDateTimeDeserializer() {}

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

}
