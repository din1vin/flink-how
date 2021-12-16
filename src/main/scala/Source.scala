import org.apache.flink.streaming.api.functions.source.SourceFunction

import java.util.concurrent.TimeUnit

class Source extends SourceFunction[Int]{

  var isRun = true

  override def run(ctx: SourceFunction.SourceContext[Int]): Unit = {
    var i = 0
    while (isRun) {
      ctx.collect(i)
      i+=1
      TimeUnit.SECONDS.sleep(2)
    }
  }

  override def cancel(): Unit = {
    isRun = false
  }
}
