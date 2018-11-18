---
layout: docs
title: Cons
permalink: /docs/optics/cons/
---

## Cons

{:.beginner}
beginner

`Cons` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between a structure `S` and its first element `A` and tail `S`.
It provides a convenient way to attach or detach elements to the beginning side of a structure [S].

It can be constructed by providing the `Prism`.

```kotlin
import arrow.data.ListK
import arrow.optics.instances.listk.cons.cons
import arrow.optics.typeclasses.Cons

val listFirst = ListK.cons<Int>().cons()
val instance = Cons(listFirst)
instance
// arrow.optics.typeclasses.Cons$Companion$invoke$1@273ec7ff
```

It defines two functions `cons` and `uncons`.

`cons` prepends an element `A` to a structure `S`.

```kotlin
import arrow.optics.instances.list.cons.cons

1.cons(listOf(2, 3))
// ListK(list=[1, 2, 3])
```

`uncons` detaches the first element `A` from a structure `S`.

```kotlin
import arrow.optics.instances.list.cons.uncons

listOf(1, 2, 3).uncons()
// Some(Tuple2(a=1, b=ListK(list=[2, 3])))
```
```kotlin
emptyList<Int>().uncons()
// None
```

### Data types

| Module | Data types |
|__arrow.data__|[ListK]({{ '/docs/arrow/data/listk' | relative_url }})|