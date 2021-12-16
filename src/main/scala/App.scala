import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

object App {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val source: DataStream[Int] = env.addSource(new Source)
    source
      .keyBy(x => x % 2)
      .flatMap(new RichFlatMapFunction[Int, String] {
        var count: ValueState[(String, Int)] = _
        var noClock: ValueState[Boolean] = _

        override def open(parameters: Configuration): Unit = {
          count = getRuntimeContext.getState(new ValueStateDescriptor("count", createTypeInformation[(String, Int)]))
          noClock = getRuntimeContext.getState(new ValueStateDescriptor[Boolean]("clock", createTypeInformation[Boolean]))
        }

        override def flatMap(value: Int, out: Collector[String]): Unit = {
          println(noClock.value() == null)
          noClock.update(false)
          out.collect(value.toString)
        }
      })
      .print()
    env.execute("test")
  }

}
