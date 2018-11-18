---
layout: docs
title: Moore
permalink: /docs/datatypes/moore/
---

## Moore

{:.intermediate}

intermediate

A `Moore` machine is a [comonadic]({{ '/docs/typeclasses/comonad' | relative_url }}) data structure which holds a state and in order to change it we need to dispatch events of some specific type. This approach is similar to the [_Elm architecture_](https://guide.elm-lang.org/architecture/) or [_Redux_](https//redux.js.org).

For creating a `Moore` machine we need its initial state and a `handle` function which will determine the inputs it can accept and how the state will change with each one.

```kotlin
import arrow.core.*
import arrow.data.*

fun handleRoute(route: String): Moore<String, Id<String>> = when (route) {
  "About" -> Moore(Id("About"), ::handleRoute)
  "Home" -> Moore(Id("Home"), ::handleRoute)
  else -> Moore(Id("???"), ::handleRoute)
}

val routerMoore = Moore(Id("???"), ::handleRoute)

routerMoore
    .handle("About")
    .extract()
    .extract()
// About
```

We also have an `extract` function which returns the current state and a `coflatMap` which transforms its type:

```kotlin
routerMoore
    .coflatMap { (view) ->
      when (view.extract()) {
        "About" -> 1
        "Home" -> 2
        else -> 0
      }
    }
    .extract()
// 0
```

### Supported type classes

| Module | Type classes |
|__arrow.typeclasses__|[Comonad]({{ '/docs/arrow/typeclasses/comonad' | relative_url }}), [Functor]({{ '/docs/arrow/typeclasses/functor' | relative_url }})|
