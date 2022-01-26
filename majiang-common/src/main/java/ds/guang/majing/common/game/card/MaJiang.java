package ds.guang.majing.common.game.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author guangmingdexin
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MaJiang implements Card {

    /**
     * 11-19 --- 代表 一万到九万
     * 101 - 109 --- 代表 一条到九条
     * 1001 - 1009 --- 代表 一筒到九筒
     */
    private int value;

    private CardType cardType;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaJiang maJiang = (MaJiang) o;
        return value == maJiang.value &&
                cardType == maJiang.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, cardType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"value\":").append(value)
                .append(", \"cardType\":").append(cardType)
                .append('}');
        return sb.toString();
    }

    @Override
    public Object value() {
        return getValue();
    }
}
