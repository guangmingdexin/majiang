package ds.guang.majing.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guangmingdexin
 */
public class LogDemo {

    private final static Logger logger = LoggerFactory.getLogger(LogDemo.class);

    public static void main(String[] args) {

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                for (int k = 1; k <= 4; k++) {
                    System.out.println(((int) (j + Math.pow(10, i))));
                }
            }
        }
    }
}
