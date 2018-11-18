---
layout: docs
title: Sum
permalink: /docs/datatypes/sum/
---

## Sum

> The sum of two Comonads is also a Comonad.

A Sum is a data structure which holds two [`Comonads`]({{ '/docs/typeclasses/comonad' | relative_url }}) and a flag for selecting which one is "active".

A common pattern used when building user interfaces is showing two tabs and allow the user to change between them. We can achieve this behavior with Sum.

For creating a Sum we need two comonadic datatypes which `extract` result type is the same. We will also need an initial side, by default it is Left.

```kotlin
import arrow.data.*
import arrow.instances.store.comonad.*

val counterStore = Store(0) { "Counter value: $it" }
val nameStore = Store("Cotel") { "Hey $it!" }

val sum = Sum(counterStore, nameStore)
sum.extract(Store.comonad(), Store.comonad())
// Counter value: 0
```

We can change the active side with `changeSide`.

```kotlin
sum.changeSide(Sum.Side.Right).extract(Store.comonad(), Store.comonad())
// Hey Cotel!
```

As Sum is also a `Comonad` we have access to `coflatmap` and `map` for changing the `extract` result.

```kotlin
val overridenSum = sum.coflatmap(Store.comonad(), Store.comonad()) {
  when (it.side) {
    is Sum.Side.Left -> "Current side is Left"
    is Sum.Side.Right -> "Current side is Right"
  }
}

overridenSum.extract(Store.comonad(), Store.comonad())
// Current side is Left
```

### Supported type classes

| Module | Type classes |
|__arrow.typeclasses__|[Comonad]({{ '/docs/arrow/typeclasses/comonad' | relative_url }}), [Functor]({{ '/docs/arrow/typeclasses/functor' | relative_url }})|
