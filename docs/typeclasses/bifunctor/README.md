---
layout: docs
title: Bifunctor
permalink: /docs/typeclasses/bifunctor/
---

## Bifunctor

{:.intermediate}
intermediate

`Bifunctor` is a lot like [`Functor`]({{ '/docs/typeclasses/functor' | relative_url }}). It offers a nice solution for those times when you don’t want to ignore the leftmost type argument of a binary type constructor, such as `Either` or `Tuple2`.

Its core operation, `bimap`, closely resembles `map`, except it lifts two functions into the new context, allowing you to apply one or both.

```kotlin
fun Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>
```

`bimap` takes two unary functions and a binary type constructor as a receiver such as `Tuple2(1, 3)` or `Left(5)` and applies whichever function it can -- both if it can!

```kotlin
import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import arrow.instances.either.bifunctor.*

fun <F> greet(BF: Bifunctor<F>, p: Kind2<F, String, String>): Kind2<F, String, String> =
    BF.run { p.bimap({ "Hello $it" }, { "General $it" }) }

greet(Either.bifunctor(), Left("there")) // Left("Hello there")    
// Left(a=Hello there)
```

```kotlin
greet(Either.bifunctor(), Right("Kenobi")) // Right("General Kenobi")
// Right(b=General Kenobi)
```

```kotlin
import arrow.instances.tuple2.bifunctor.*

greet(Tuple2.bifunctor(), Tuple2("there", "Kenobi")) // Tuple2("Hello there", "General Kenobi")
// Tuple2(a=Hello there, b=General Kenobi)
```

So, `bimap` is `map` but for binary type constructors where you want the ability to lift two functions at once.

### Main Combinators

#### Kind2<F, A, B>#bimap

Transforms the inner contents of a binary type constructor

`fun Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>`

```kotlin
val tuple2Bifunctor = Tuple2.bifunctor()
tuple2Bifunctor.run { Tuple2(4, 4).bimap({ it + 1 }, { it - 1 }) }
// Tuple2(a=5, b=3)
```

#### Other combinators

For a full list of other useful combinators available in `Bifunctor` see the [Source][bifunctor_source]{:target="_blank"}

### Laws

Arrow provides [`BifunctorLaws`][bifunctor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Bifunctor instances.

### Data types

| Module | Data types |
|__arrow.core__|[Either]({{ '/docs/arrow/core/either' | relative_url }}), [Tuple2]({{ '/docs/arrow/core/tuple2' | relative_url }})|
|__arrow.data__|[Ior]({{ '/docs/arrow/data/ior' | relative_url }})|

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>



[bifunctor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Bifunctor.kt
[bifunctor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/BifunctorLaws.kt
