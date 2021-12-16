import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;


public class CyclicalCall<K, I, O> extends KeyedProcessFunction<K, I, O> {
    private ValueState<Boolean> hasTimer;
    private static final int FIVE_MINUTE = 5 * 60 * 1000;


    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<K, I, O>.OnTimerContext ctx, Collector<O> out) throws Exception {
        invokeFunction();
        ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + FIVE_MINUTE);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        hasTimer = getRuntimeContext().getState(new ValueStateDescriptor<Boolean>("timer", Types.BOOLEAN));
    }

    @Override
    public void processElement(I value, KeyedProcessFunction<K, I, O>.Context ctx, Collector<O> out) throws Exception {
        if (hasTimer.value() == null) {
            hasTimer.update(true);
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + FIVE_MINUTE);
        }
    }

    private void invokeFunction() {
        //TODO 到点触发的动作
    }
}
