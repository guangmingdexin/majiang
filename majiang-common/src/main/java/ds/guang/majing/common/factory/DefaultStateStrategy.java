package ds.guang.majing.common.factory;

import ds.guang.majing.common.DsResult;
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
    public State<String, String, DsResult> newState(Supplier<String> stateId, Supplier<String> eventId, Supplier<State.Handler<DsResult>> handler) {

        String e = eventId.get();

        State.Handler<DsResult> r = handler.get();

        return new AbstractStateImpl<String, String, DsResult>(stateId.get()) {
            @Override
            public State<String, String, DsResult> onEvent(String eventId1, Handler<DsResult> handler1) {
                return super.onEvent(e, r);
            }
        };
    }

    @Override
    public State<String, String, DsResult> newState(Supplier<String> stateId) {
        return newState(stateId, () ->  "EVENT_EMPTY_ID",  () -> data -> DsResult.empty());
    }
}
