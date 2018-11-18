package arrow.typeclasses

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.instances.list.foldable.fold
import arrow.instances.monoid
import arrow.instances.monoid.invariant.invariant
import arrow.instances.option.monoidK.monoidK
import arrow.test.UnitSpec
import arrow.test.laws.InvariantLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoidKTest : UnitSpec() {

    init {
        val result: Kind<ForOption, Int> = listOf(Option(1)).fold(Option.monoidK().algebra())
        assertEquals(result, Option(1))
    }
}