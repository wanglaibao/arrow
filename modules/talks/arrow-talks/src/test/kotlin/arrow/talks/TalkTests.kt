package arrow.talks

import arrow.effects.MaybeK
import arrow.effects.k
import arrow.effects.monadError
import arrow.effects.value
import arrow.test.UnitSpec
import arrow.typeclasses.bindingCatch
import io.kotlintest.KTestJUnitRunner
import io.reactivex.Maybe
import io.reactivex.observers.TestObserver
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class TalkTests : UnitSpec() {

  init {
    "Multi-thread Maybes finish correctly" {
      val value: Maybe<Long> = MaybeK.monadError().bindingCatch {
        val a = Maybe.timer(2, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
    }
  }
}
