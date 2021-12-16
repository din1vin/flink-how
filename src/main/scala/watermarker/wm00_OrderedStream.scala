package watermarker

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * 演示没有WaterMarker的有序数据处理
 */
object wm00_OrderedStream extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

}
