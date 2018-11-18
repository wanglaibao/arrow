---
layout: docs
title: Getter
permalink: /docs/optics/getter/
---

## Getter

{:.beginner}
beginner

A `Getter` is an optic that can focus into a structure and `get` its focus.
It can be seen as a wrapper of a get function `(S) -> A` that can be composed with other optics.

Creating a `Getter` can be done by referencing a property of a data classes or by providing a function.

```kotlin
import arrow.optics.*
import arrow.*

data class Player(val health: Int)

val healthGetter = Getter(Player::health)
val player = Player(75)
healthGetter.get(player)
// 75
```
```kotlin
import arrow.data.*

fun <T> nonEmptyListHead() = Getter<NonEmptyList<T>, T> {
    it.head
}

nonEmptyListHead<Int>().get(NonEmptyList.of(1, 2, 3, 4))
// 1
```

Or from any of the optics defined in `arrow-optics` that allow to safely getting its focus.

```kotlin
import arrow.core.*
import arrow.optics.instances.*

val headGetter: Getter<NonEmptyList<String>, String> = NonEmptyList.head<String>().asGetter()
val tupleGetter: Getter<Tuple2<String, Int>, String> = Tuple2.first<String, Int>().asGetter()
```

`Getter` also has some convenience methods to make working with [Reader]({{ '/docs/datatypes/reader' | relative_url }}) easier.

```kotlin
val reader: Reader<NonEmptyList<String>, String> = NonEmptyList.head<String>().asGetter().ask()

reader
  .map(String::toUpperCase)
  .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))
// HELLO
```

```kotlin
NonEmptyList.head<String>().asGetter().asks(String::decapitalize)
  .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))
// hello
```

There are also some convenience methods to make working with [State]({{ '/docs/datatypes/state' | relative_url }}) easier.

```kotlin
import arrow.data.*

val inspectHealth = healthGetter.extract()
inspectHealth.run(player)
// Tuple2(a=Player(health=75), b=75)
```

```kotlin
val takeMedpack = healthGetter.extractMap { it + 25 }
takeMedpack.run(player)
// Tuple2(a=Player(health=75), b=100)
```

## Composition

Unlike a regular `get` function a `Getter` composes. Similar to a `Lens` we can compose `Getter`s to create telescopes and zoom into nested structures.

```kotlin
val firstBar: Getter<NonEmptyList<Player>, Int> = NonEmptyList.head<Player>() compose healthGetter
firstBar.get(Player(5).nel())
// 5
```

`Getter` can be composed with `Getter`, `Iso`, `Lens` and `Fold` and the composition results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Getter | Getter | Getter | X | X | Getter | X | Fold | X |

### Supported Type Classes

| Module | Type classes |
