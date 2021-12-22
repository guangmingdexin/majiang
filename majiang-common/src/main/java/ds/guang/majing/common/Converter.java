package ds.guang.majing.common;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * @author asus
 */
public final class Converter extends StdConverter<Object, String> {

    public Converter() {
    }

    @Override
    public String convert(Object o) {
        return "******";
    }
}
