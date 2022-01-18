package ds.guang.majing.common.game.rule;

/**
 *
 * 规则设定接口
 * 此接口应该规定了不同游戏的不同规则，
 * 例如四人川麻（换牌），四人湖南麻将（包括 吃，不换牌），甚至三人麻将
 * 预期效果可以通过设定不同的规则达成不同的麻将效果
 *
 * @author guangyong.deng
 * @date 2021-12-17 13:53
 */
public interface Rule<T, R> {

    /**
     *
     * 通过传入不同的游戏类型返回不同的规则
     *
     * @param t 游戏类型
     * @return
     */
    Rule<T, R> create(T t);

    /**
     * 获取规则执行状态机
     *
     * @return R
     */
    R getRuleActor();
}
