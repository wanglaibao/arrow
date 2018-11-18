---
layout: docs
title: Async
permalink: /docs/effects/async/
---

## Async

{:.intermediate}
intermediate

Being able to run code in a different context of execution (i.e. thread) than the current one implies that, even if it's part of a sequence, the code will have to be asynchronous.
Running asynchronous code always requires a callback after completion on error capable of returning to the current thread.

The same way the typeclass [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) represents a sequence of events, and [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) a sequence that can fail, the typeclass `Async` represents asynchronous code with a callback.
Examples of that can run code asynchronously are typically datatypes that can suspend effects, and delay evaluation.

```kotlin
import arrow.*
import arrow.core.*
import arrow.effects.*
import arrow.effects.instances.io.async.*

IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    callback(1.right())
  }.fix().attempt().unsafeRunSync()
// Right(b=1)
```

```kotlin
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    callback(RuntimeException().left())
  }.fix().attempt().unsafeRunSync()
// Left(a=java.lang.RuntimeException)
```

`Async` includes all combinators present in [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}).

### Main Combinators

#### async

Receives a function returning `Unit` with a callback as a parameter.
The function is responsible of calling the callback once it obtains a result.
The callback accepts `Either<Throwable, A>` as the return, where the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution and the right side is the completion value of the operation.

```kotlin
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    userFetcherWithCallback("1").startAsync({ user: User ->
      callback(user.left())
    }, { error: Exception ->
      callback(error.right())
    })
  }
```

```kotlin
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    userFromDatabaseObservable().subscribe({ user: User ->
      callback(user.left())
    }, { error: Exception ->
      callback(error.right())
    })
  }
```

#### continueOn

It makes the rest of the operator chain to be executed on a separate `CoroutineContext`, effectively jumping threads if necessary.

```kotlin
IO.async().run {
  // In current thread
  just(createUserFromId(123))
    .continueOn(CommonPool)
    // In CommonPool
    .flatMap { request(it) }
    .continueOn(Ui)
    // In Ui
    .flatMap { showResult(it) }
}
```

Behind the scenes `continueOn()` starts a new coroutine and passes the rest of the chain as the block to execute.

The function `continueOn()` is also available inside [`Monad Comprehensions`]({{ '/docs/patterns/monad_comprehensions' | relative_url }}).

#### invoke with CoroutineContext

Similar to `MonadDefer`'s `invoke`, this constructor it takes a single generator function and the `CoroutineContext` it has to be run on.

```kotlin
IO.async().run {
  // In current thread
  invoke(CommonPool) {
    // In CommonPool
    requestSync(createUserFromId(123))
  }
}
```

#### defer with CoroutineContext

Similar to `MonadDefer`'s `defer`, this constructor it takes a single function returning a `Kind<F, A>` and the `CoroutineContext` it has to be run on.

```kotlin
IO.async().run {
  // In current thread
  defer(CommonPool) {
    // In CommonPool
    async { cb ->
      requestAsync(createUserFromId(123), cb)
    }
  }
}
```

#### never

Creates an object using `async()` whose callback is never called.

Depending on how the datatype is implemented this may cause unexpected errors like awaiting forever for a result.

Use with *SEVERE CAUTION*.

```kotlin
IO.async()
  .never()
  .unsafeRunSync()
// ERROR!! The program blocks the current thread forever.
```

> never() exists to test datatypes that can handle non-termination.
For example, IO has unsafeRunTimed that runs never() safely.

### Laws

Arrow provides `AsyncLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Async` instances.

### Data types

| Module | Data types |
|__arrow.effects__|[DeferredK]({{ '/docs/arrow/effects/deferredk' | relative_url }}), [FlowableK]({{ '/docs/arrow/effects/flowablek' | relative_url }}), [FluxK]({{ '/docs/arrow/effects/fluxk' | relative_url }}), [MaybeK]({{ '/docs/arrow/effects/maybek' | relative_url }}), [MonoK]({{ '/docs/arrow/effects/monok' | relative_url }}), [ObservableK]({{ '/docs/arrow/effects/observablek' | relative_url }}), [SingleK]({{ '/docs/arrow/effects/singlek' | relative_url }}), [IO]({{ '/docs/arrow/effects/io' | relative_url }})|

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

