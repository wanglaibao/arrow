---
layout: docs
title: Snoc
permalink: /docs/optics/snoc/
---

## Snoc

{:.beginner}
beginner

`Snoc` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between `S` and its init `A` and last element `S`.
`Snoc` can be seen as the reverse of [Cons](({{ '/docs/optics/cons' | relative_url }})), it provides a way to attach or detach elements on the end side of a structure.

It can be constructed by providing the `Prism`.

```kotlin
import arrow.data.ListK
import arrow.optics.instances.listk.snoc.snoc
import arrow.optics.typeclasses.Snoc

val listLast = ListK.snoc<Int>().snoc()
val instance = Snoc(listLast)
instance
// arrow.optics.typeclasses.Snoc$Companion$invoke$1@32cc7136
```

It defines two functions `snoc` and `unsnoc`.

`snoc` appends an element `A` to a structure `S`.

```kotlin
import arrow.optics.instances.list.snoc.snoc

listOf(1, 2).snoc(3)
// ListK(list=[1, 2, 3])
```

`unsnoc` detaches the last element `A` from a structure `S`.

```kotlin
import arrow.optics.instances.list.snoc.unsnoc

listOf(1, 2, 3).unsnoc()
// Some(Tuple2(a=ListK(list=[1, 2]), b=3))
```
```kotlin
emptyList<Int>().unsnoc()
// None
```

### Data types

| Module | Data types |
|__arrow.data__|[ListK]({{ '/docs/arrow/data/listk' | relative_url }})|
