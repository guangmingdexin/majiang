package ds.guang.majing.common.util;

import java.util.UUID;

public final class StringUtil {


    public static String generateIdUUid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
