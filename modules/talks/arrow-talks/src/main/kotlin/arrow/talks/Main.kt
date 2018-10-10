package arrow.talks

import arrow.effects.IO

interface Logger<F> {
  fun stringify(f: F) =
    f.toString()
}

interface IOLogger : Logger<IO<Int>>

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    println(object : IOLogger {}.stringify(IO.just(1)))
  }
}
