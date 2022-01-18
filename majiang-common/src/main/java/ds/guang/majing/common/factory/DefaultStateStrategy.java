package ds.guang.majing.common.factory;

import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;

import java.util.function.Supplier;

/**
 * @author guangyong.deng
 * @date 2021-12-23 9:35
 */
public class DefaultStateStrategy implements StateStrategy<String, String, DsResult> {

    static final DefaultStateStrategy INSTANCE = new DefaultStateStrategy();

    @Override
    public State<String, String, DsResult> newState(Supplier<String> stateIdSupplier,
                                                    Supplier<String> eventIdSupplier,
                                                    Supplier<State.Handler<DsResult>> handlerSupplier) {

        String e = eventIdSupplier.get();
        State.Handler<DsResult> r = handlerSupplier.get();

        State<String, String, DsResult> state = new AbstractStateImpl<String, String, DsResult>(stateIdSupplier.get()) {};

        state.onEvent(e, r);

        return state;
    }

    @Override
    public State<String, String, DsResult> newState(Supplier<String> stateIdSupply) {
        return newState(stateIdSupply, () ->  "EVENT_EMPTY_ID",  () -> data -> DsResult.empty());
    }
}
