package ds.guang.majiang.server.layer;

import ds.guang.majing.common.util.ClassUtil;
import ds.guang.majing.common.state.State;

/**
 *
 * 单纯的服务器回调接口封装,算是对 State 的一个增强和封装
 *
 * @author guangyong.deng
 * @date 2021-12-23 14:48
 */
public interface Action {

    /**
     *
     * 对 State 进行一些设置
     * @param state 状态
     *
     */
    void handler(State state);


    /**
     *
     * 获取优先级
     *
     * @return 优先级
     */
    default int getOrder() {

        if(ClassUtil.exits(this.getClass(), StateMatchAction.class)) {
            StateMatchAction annotation = this.getClass().getAnnotation(StateMatchAction.class);
            return annotation.order();
        }
        // 不设置默认优先级最低
        return Integer.MIN_VALUE;
    }

}
