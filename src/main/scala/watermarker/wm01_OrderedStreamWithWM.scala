package watermarker

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

import java.time.Duration

/**
 * waterMarker 保证乱序数据正确落窗
 */
object wm01_OrderedStreamWithWM extends App {

  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  //在flink 1.12版本被废弃,默认时间语义为EventTime
  //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

  env.addSource[(String, Long)](new SourceFunction[(String, Long)] {
    override def run(ctx: SourceFunction.SourceContext[(String, Long)]): Unit = {
      ctx.collect("key", 0L)
      ctx.collect("key", 1000L)
      ctx.collect("key", 3000L)
      ctx.collect("key", 2000L)
      ctx.collect("key", 4000L)
      ctx.collect("key", 6000L)
      ctx.collect("key", 5000L)
      ctx.collect("key", 7000L)
      ctx.collect("key", 8000L)
      ctx.collect("key", 10000L)
      ctx.collect("key", 9000L)
    }

    override def cancel(): Unit = {

    }
  }).assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness[(String, Long)](Duration.ofSeconds(1))
    .withTimestampAssigner(new SerializableTimestampAssigner[(String, Long)] {
      override def extractTimestamp(element: (String, Long), recordTimestamp: Long): Long = element._2
    }))
    .keyBy(_._1)
    .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    .sum(1)
    .print()
  env.execute("watermark test")
}
